package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.domain.model.ChatAttatchmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatAttachmentRepository : JpaRepository<ChatAttatchmentEntity, String> {
}