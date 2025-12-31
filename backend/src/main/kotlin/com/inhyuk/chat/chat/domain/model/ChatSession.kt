package com.inhyuk.chat.chat.domain.model

import com.inhyuk.chat.chat.domain.ChatSessionProvider
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ChatMessageDeserializer
import dev.langchain4j.service.TokenStream
import java.time.LocalDateTime

/**
 * 캐시된 세션 정보
 */
data class ChatSession(
    val sessionEntity: ChatSessionEntity,
    val memoryEntity: ChatMemoryEntity,
    var lastAccessTime: LocalDateTime = LocalDateTime.now()
) {
    val id = sessionEntity.id
    val userId = sessionEntity.userId
    val messages
        get() = if (memoryEntity.messages.isEmpty()) mutableListOf<ChatMessage>() else ChatMessageDeserializer.messagesFromJson(memoryEntity.messages)

    var activeTokenStream: ActiveTokenStream? = null
        private set

    val isActive: Boolean
        get() = activeTokenStream != null

    fun isExpired(): Boolean {
        return lastAccessTime.plusMinutes(ChatSessionProvider.SESSION_CACHE_TTL_MINUTES)
            .isBefore(LocalDateTime.now())
    }

    fun setActiveTokenStream(tokenStream: TokenStream) : ActiveTokenStream {
        val newActiveTokenStream = ActiveTokenStream(tokenStream)
        newActiveTokenStream.onCompleteResponse { activeTokenStream = null }
        activeTokenStream = newActiveTokenStream
        return newActiveTokenStream
    }

    fun updateAccessTime() {
        lastAccessTime = LocalDateTime.now()
    }


}