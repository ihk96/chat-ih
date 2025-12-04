package com.inhyuk.chat.domain.chat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ChatSessionRepository : JpaRepository<ChatSessionEntity, String> {

}