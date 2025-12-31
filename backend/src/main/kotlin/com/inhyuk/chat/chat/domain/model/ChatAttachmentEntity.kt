package com.inhyuk.chat.chat.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "chat_attatchments")
@EntityListeners(AuditingEntityListener::class)
class ChatAttachmentEntity (
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    val id : String? =null,
    val fileId : String,
    val fileName : String,
    val contentType: AttachmentContentType,
    val mimeType : String,
    var chatSessionId : String? = null,
    var chatMessageId : String? = null,
    @Column(columnDefinition = "TEXT")
    var extractedText: String? = null,
){
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null

    var isUsed : Boolean = false
}

enum class AttachmentContentType { IMAGE, PDF, DOCUMENT, OTHER }