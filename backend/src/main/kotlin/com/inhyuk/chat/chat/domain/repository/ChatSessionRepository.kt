package com.inhyuk.chat.chat.domain.repository

import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatSessionRepository : JpaRepository<ChatSessionEntity, String> {
    fun findAllByUserId(userId: String, pageable: Pageable): Page<ChatSessionEntity>
}