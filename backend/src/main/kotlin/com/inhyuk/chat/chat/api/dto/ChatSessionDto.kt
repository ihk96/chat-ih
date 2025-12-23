package com.inhyuk.chat.chat.api.dto

import com.inhyuk.chat.chat.domain.model.ChatSession
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessageType
import dev.langchain4j.data.message.UserMessage

data class ChatSessionDto (
    private val session : ChatSession
) {
    val id : String = session.id
    val userId : String = session.userId
    val title: String = session.entity.title
    val messages : List<ChatMeesageDto> = if (session.messages.isEmpty()) emptyList() else session.messages.map {
        if(it.type() == ChatMessageType.USER) ChatMeesageDto.user(it as UserMessage) else ChatMeesageDto.ai(it as AiMessage)
    }
}

data class ChatMeesageDto(
    val text: String?,
    val thinking: String?,
    val toolRequests: List<String>?,
    val type : ChatMessageType
) {
    companion object {
        fun user(userMessage: UserMessage) : ChatMeesageDto {
            return ChatMeesageDto(
                text = userMessage.singleText(),
                thinking = null,
                toolRequests = null,
                type = ChatMessageType.USER
            )
        }
        fun ai(aiMessage: AiMessage) : ChatMeesageDto {
            return ChatMeesageDto(
                text = aiMessage.text(),
                thinking = aiMessage.thinking(),
                toolRequests = if(aiMessage.hasToolExecutionRequests()) aiMessage.toolExecutionRequests().map { it.name() } else emptyList(),
                type = ChatMessageType.AI
            )
        }
    }
}