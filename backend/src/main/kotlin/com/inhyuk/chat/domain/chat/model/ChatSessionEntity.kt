package com.inhyuk.chat.domain.chat.model

import com.inhyuk.chat.common.JsonbConverter
import dev.langchain4j.data.message.ChatMessage
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table()
class ChatSessionEntity (
    @Id
    val id: String,
    val userId : String,
    
    var title: String = "New Chat",

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter::class)
    var messages : MutableList<ChatMessage> = mutableListOf(),

    val createdDate : LocalDateTime = LocalDateTime.now(),
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()
)