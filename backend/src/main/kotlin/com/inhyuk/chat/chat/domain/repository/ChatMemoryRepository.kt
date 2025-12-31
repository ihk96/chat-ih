package com.inhyuk.chat.chat.domain.repository

import com.inhyuk.chat.chat.domain.model.ChatMemoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMemoryRepository : JpaRepository<ChatMemoryEntity, String>{
    fun findByChatSessionId(chatSessionId: String): ChatMemoryEntity?
}