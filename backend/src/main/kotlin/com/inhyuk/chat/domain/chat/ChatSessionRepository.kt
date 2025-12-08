package com.inhyuk.chat.domain.chat

import com.inhyuk.chat.domain.chat.model.ChatSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository



import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@Repository
interface ChatSessionRepository : JpaRepository<ChatSessionEntity, String> {
    fun findAllByUserId(userId: String, pageable: Pageable): Page<ChatSessionEntity>
}