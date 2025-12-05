package com.inhyuk.chat.domain.chat

import com.inhyuk.chat.domain.chat.model.ChatSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ChatSessionRepository : JpaRepository<ChatSessionEntity, String> {

}