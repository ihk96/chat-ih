package com.inhyuk.chat.chat.domain.model

import com.inhyuk.chat.chat.domain.ChatSessionProvider
import dev.langchain4j.service.TokenStream
import java.time.LocalDateTime

/**
 * 캐시된 세션 정보
 */
data class ChatSession(
    val entity: ChatSessionEntity,
    var lastAccessTime: LocalDateTime = LocalDateTime.now()
) {
    val id = entity.id
    val userId = entity.userId
    val messages
        get() = entity.messages.toMutableList()

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