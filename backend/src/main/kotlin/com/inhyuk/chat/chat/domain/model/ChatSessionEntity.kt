package com.inhyuk.chat.chat.domain.model

import com.inhyuk.chat.common.util.JsonbConverter
import dev.langchain4j.data.message.ChatMessage
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name="chat_sessions")
@EntityListeners(AuditingEntityListener::class)
class ChatSessionEntity (
    @Id
    val id: String,
    val userId : String,
    
    var title: String = "New Chat",

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter::class)
    var messages : MutableList<ChatMessage> = mutableListOf(),

){
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}