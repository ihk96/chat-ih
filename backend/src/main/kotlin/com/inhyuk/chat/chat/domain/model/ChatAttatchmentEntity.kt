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
class ChatAttatchmentEntity (
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    val id : String,
    val fileId : String?,
    val contentType: AttachmentContentType,
    val chatSessionId : String,
    val chatMessageId : String,
    @Column(columnDefinition = "TEXT")
    var extractedText: String? = null,
){
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}

enum class AttachmentContentType { IMAGE, PDF, DOCUMENT, OTHER }