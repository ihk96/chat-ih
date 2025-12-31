package com.inhyuk.chat.chat.domain.service

import com.inhyuk.chat.chat.domain.ChatSessionProvider
import com.inhyuk.chat.chat.domain.assistant.BasicStreamAssistant
import com.inhyuk.chat.chat.domain.model.ActiveTokenStream
import com.inhyuk.chat.chat.domain.model.ChatMessageEntity
import com.inhyuk.chat.chat.domain.model.ChatSession
import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import com.inhyuk.chat.chat.domain.repository.ChatMessageRepository
import dev.langchain4j.data.message.ChatMessageType
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.service.AiServices
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatService(
    private val sessionProvider: ChatSessionProvider,
    private val chatAttachmentService: ChatAttachmentService,
    private val chatMessageRepository: ChatMessageRepository,
) {

    fun chatStream(chatSession: ChatSession, message : String, model : StreamingChatModel, attachments : List<String>? = null) : ActiveTokenStream {
        val id = chatSession.id

        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(500)
                .chatMemoryStore(sessionProvider)
                .build()
        }

        val assistant = AiServices.builder(BasicStreamAssistant::class.java)
            .streamingChatModel(model)
            .chatMemoryProvider(chatMemoryProvider)
            .build()

        val chatMessageEntity = ChatMessageEntity(
            chatSessionId = chatSession.id,
            message = message,
            messageType = ChatMessageType.USER,
            attachments = attachments ?: emptyList()
        )
        chatMessageRepository.save(chatMessageEntity)

        val contents = attachments?.let { chatAttachmentService.convertAttachmentsToContents(attachments) } ?: emptyList()
        val tokenStream = assistant.chat(id, message, contents)

        return chatSession.setActiveTokenStream(tokenStream)
    }

    fun addNewChatSession(userId : String) : ChatSessionEntity {
        val newSession = ChatSessionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
        )
        sessionProvider.saveSession(newSession)
        return newSession
    }

}