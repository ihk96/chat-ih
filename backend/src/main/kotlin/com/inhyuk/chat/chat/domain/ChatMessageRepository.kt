package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.domain.model.ChatMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessageEntity, String> {
}