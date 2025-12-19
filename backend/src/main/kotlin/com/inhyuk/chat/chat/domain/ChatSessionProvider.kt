package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.domain.model.ChatSession
import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import com.inhyuk.chat.chat.domain.ChatSessionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import dev.langchain4j.data.message.ChatMessage
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
    private val repository: ChatSessionRepository
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
        val entity = repository.findById(sessionId).orElse(null) ?: return null

        // 캐시에 저장
        sessionCache[sessionId] = ChatSession(entity)
        return ChatSession(entity)
    }

    /**
     * 세션 정보 저장
     */
    fun saveSession(entity: ChatSessionEntity) {
        repository.save(entity)
        sessionCache[entity.id] = ChatSession(entity)
    }

    /**
     * 세션 삭제
     */
    fun deleteSession(sessionId: String) {
        repository.deleteById(sessionId)
        sessionCache.remove(sessionId)
    }

    fun getSessions(userId: String, pageable: Pageable): Page<ChatSession> {
        return repository.findAllByUserId(userId, pageable).map { ChatSession(it) }
    }

    // ==================== ChatMemoryStore 구현 ====================

    override fun getMessages(memoryId: Any?): MutableList<out ChatMessage?>? {
        val sessionId = memoryId as? String ?: return null

        // 캐시에서 조회
        val cached = sessionCache[sessionId]
        if (cached != null) {
            return cached.messages.toMutableList()
        }

        // DB에서 조회
        val entity = repository.findById(sessionId).orElse(null) ?: return null
        val messages = entity.messages.toMutableList()

        // 캐시에 저장
        sessionCache[entity.id] = ChatSession(entity)
        return messages
    }

    override fun updateMessages(memoryId: Any?, messages: MutableList<ChatMessage?>) {
        val sessionId = memoryId as? String ?: return

        // 캐시에서 조회
        val cached = sessionCache[sessionId]
        if (cached != null) {
            // 캐시 업데이트
            cached.entity.messages = messages.filterNotNull().toMutableList()
        }


        // DB 업데이트
        repository.findById(sessionId).ifPresent { entity ->
            entity.messages = messages.filterNotNull().toMutableList()
            entity.lastModifiedDate = LocalDateTime.now()
            repository.save(entity)
            // 캐시에 저장
            sessionCache[entity.id] = ChatSession(entity)
        }
    }

    override fun deleteMessages(memoryId: Any?) {
        val sessionId = memoryId as? String ?: return
        // 캐시에서 조회
        val cached = sessionCache[sessionId]
        if (cached != null) {
            // 캐시 업데이트
            cached.entity.messages.clear()
        }


        // DB 업데이트
        repository.findById(sessionId).ifPresent { entity ->
            entity.lastModifiedDate = LocalDateTime.now()
            repository.save(entity)
            // 캐시에 저장
            sessionCache[entity.id] = ChatSession(entity)
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
