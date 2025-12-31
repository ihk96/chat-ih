package com.inhyuk.chat.chat.api.dto

import com.inhyuk.chat.chat.domain.model.ChatSession
import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessageType
import dev.langchain4j.data.message.UserMessage

data class ChatSessionDto(
    val id : String,
    val title: String,
    val userId : String
){
    constructor(sessionEntity : ChatSessionEntity) : this(
        id = sessionEntity.id,
        title = sessionEntity.title,
        userId = sessionEntity.userId
    )
}

data class ChatMessageDto(
    val text: String?,
    val thinking: String?,
    val toolRequests: List<String>?,
    val type : ChatMessageType,
    val attachments : List<ChatAttachmentResponseDto>? = null
) {
    companion object {
        fun user(userMessage: UserMessage) : ChatMessageDto {
            return ChatMessageDto(
                text = userMessage.singleText(),
                thinking = null,
                toolRequests = null,
                type = ChatMessageType.USER
            )
        }
        fun ai(aiMessage: AiMessage) : ChatMessageDto {
            return ChatMessageDto(
                text = aiMessage.text(),
                thinking = aiMessage.thinking(),
                toolRequests = if(aiMessage.hasToolExecutionRequests()) aiMessage.toolExecutionRequests().map { it.name() } else emptyList(),
                type = ChatMessageType.AI
            )
        }
    }
}