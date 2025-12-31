package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.domain.model.ChatMemoryEntity
import com.inhyuk.chat.chat.domain.model.ChatMessageEntity
import com.inhyuk.chat.chat.domain.model.ChatSession
import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import com.inhyuk.chat.chat.domain.repository.ChatMemoryRepository
import com.inhyuk.chat.chat.domain.repository.ChatMessageRepository
import com.inhyuk.chat.chat.domain.repository.ChatSessionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ChatMessageDeserializer
import dev.langchain4j.data.message.ChatMessageSerializer
import dev.langchain4j.data.message.ChatMessageType
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 통합 채팅 세션 관리자
 *
 * 역할:
 * 1. 세션 정보 캐싱 및 관리
 * 2. 채팅 메모리 저장소 (ChatMemoryStore 구현)
 * 3. 토큰 스트림 관리 (현재 진행 중인 스트림)
 * 4. 캐시 정리 (TTL 기반)
 * 5. 동시성 안전 처리
 */
@Component
class ChatSessionProvider(
    private val sessionRepository: ChatSessionRepository,
    private val memoryRepository: ChatMemoryRepository,
    private val chatMessageRepository: ChatMessageRepository
) : ChatMemoryStore {

    private val logger = LoggerFactory.getLogger(ChatSessionProvider::class.java)

    // 세션 정보 캐시 (thread-safe)
    private val sessionCache = ConcurrentHashMap<String, ChatSession>()

    // 캐시 정리 스케줄러
    private val cleanupScheduler = Executors.newSingleThreadScheduledExecutor()

    // 설정값
    companion object {
        const val SESSION_CACHE_TTL_MINUTES = 30L
        const val MEMORY_CACHE_TTL_MINUTES = 60L
        const val CLEANUP_INTERVAL_MINUTES = 5L
    }

    init {
        // 주기적으로 만료된 캐시 정리
        cleanupScheduler.scheduleAtFixedRate(
            ::cleanupExpiredCaches,
            CLEANUP_INTERVAL_MINUTES,
            CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
    }

    // ==================== 세션 관리 ====================

    /**
     * 세션 정보 조회 (캐시 우선, 없으면 DB 조회)
     */
    fun getSession(sessionId: String): ChatSession? {
        val cached = sessionCache[sessionId]
        if (cached != null && !cached.isExpired()) {
            cached.updateAccessTime()
            return cached
        }

        // DB에서 조회
        val entity = sessionRepository.findById(sessionId).orElseThrow { IllegalArgumentException("Session not found")}
        val memory = memoryRepository.findByChatSessionId(sessionId) ?: let{
            val newMemory = ChatMemoryEntity(chatSessionId = sessionId)
            memoryRepository.save(newMemory)
            newMemory
        }

        val session = ChatSession(
            sessionEntity = entity,
            memoryEntity = memory,
        )
        // 캐시에 저장
        sessionCache[sessionId] = session
        return session
    }

    /**
     * 세션 정보 저장
     */
    fun saveSession(entity: ChatSessionEntity) {
        sessionRepository.save(entity)
        val memory = memoryRepository.findByChatSessionId(entity.id) ?: let{
            val newMemory = ChatMemoryEntity(chatSessionId = entity.id)
            memoryRepository.save(newMemory)
            newMemory
        }
        sessionCache[entity.id] = ChatSession(entity, memory)
    }

    /**
     * 세션 삭제
     */
    fun deleteSession(sessionId: String) {
        sessionRepository.deleteById(sessionId)
        sessionCache.remove(sessionId)
        memoryRepository.findByChatSessionId(sessionId)?.let { memoryRepository.delete(it) }
    }

    // ==================== ChatMemoryStore 구현 ====================

    override fun getMessages(memoryId: Any?): MutableList<out ChatMessage?>? {
        val sessionId = memoryId as? String ?: return null
        logger.info("Retrieving messages for session: $sessionId")
        // 캐시에서 조회
        val cached = sessionCache[sessionId]
        if (cached != null) {
            logger.info("Retrieved messages from cache:")
            return cached.messages
        }

        // DB에서 조회
        logger.info("Retrieved messages from DB:")
        val session = getSession(sessionId) ?: throw IllegalArgumentException("Session not found")
        logger.info("Cached messages for session: $sessionId")
        return session.messages
    }

    override fun updateMessages(memoryId: Any?, messages: MutableList<ChatMessage?>) {
        val sessionId = memoryId as? String ?: return
        logger.info("Updating messages for session: $sessionId")

        // 캐시에서 조회
        val cached = sessionCache[sessionId]
        if (cached != null) {
            logger.info("Updating cached messages for session: $sessionId")
            // 캐시 업데이트
            cached.memoryEntity.messages = ChatMessageSerializer.messagesToJson(messages)
        }


        // DB 업데이트
        memoryRepository.findByChatSessionId(sessionId)?.let { it ->
            it.messages = ChatMessageSerializer.messagesToJson(messages)
            memoryRepository.save(it)
        }

        messages.last()?.let {
            if(it.type().equals(ChatMessageType.AI)) {
                val chatMessageEntity = ChatMessageEntity(
                    chatSessionId = sessionId,
                    message = ChatMessageSerializer.messageToJson(it),
                    messageType = it.type(),
                )
                chatMessageRepository.save(chatMessageEntity)
            }
        }
    }

    override fun deleteMessages(memoryId: Any?) {
        val sessionId = memoryId as? String ?: return
        // 캐시에서 조회
        val cached = sessionCache[sessionId]
        if (cached != null) {
            // 캐시 업데이트
            cached.memoryEntity.messages = ""
        }

    }

    // ==================== 캐시 정리 ====================

    private fun cleanupExpiredCaches() {
        try {
            val now = LocalDateTime.now()

            // 세션 캐시 정리
            val expiredSessions = sessionCache.entries.filter { it.value.isExpired() }
            expiredSessions.forEach { sessionCache.remove(it.key) }

            if (expiredSessions.isNotEmpty()) {
                logger.info("Cleaned up ${expiredSessions.size} expired session caches")
            }

        } catch (e: Exception) {
            logger.error("Error during cache cleanup", e)
        }
    }

    /**
     * 수동 전체 캐시 정리 (테스트/관리 용도)
     */
    fun clearAllCaches() {
        sessionCache.clear()
        logger.info("Cleared all caches")
    }

    // ==================== Shutdown ====================

    fun shutdown() {
        cleanupScheduler.shutdown()
        try {
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow()
            }
        } catch (e: InterruptedException) {
            cleanupScheduler.shutdownNow()
            Thread.currentThread().interrupt()
        }
        logger.info("ChatSessionManager shutdown complete")
    }
}
