package com.inhyuk.chat.chat.domain.model

import dev.langchain4j.data.message.ChatMessageType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.context.event.EventListener
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
@EntityListeners(AuditingEntityListener::class)
class ChatMessageEntity (
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    val id : String? = null,
    val chatSessionId : String,
    @Column(columnDefinition = "TEXT")
    val message : String,
    val messageType : ChatMessageType,
    val attatchments : List<String> = mutableListOf()
) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}