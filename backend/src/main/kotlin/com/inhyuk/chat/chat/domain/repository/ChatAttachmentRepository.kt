package com.inhyuk.chat.chat.domain.repository

import com.inhyuk.chat.chat.domain.model.ChatAttachmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatAttachmentRepository : JpaRepository<ChatAttachmentEntity, String> {
}