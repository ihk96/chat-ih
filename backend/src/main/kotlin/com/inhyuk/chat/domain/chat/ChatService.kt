package com.inhyuk.chat.domain.chat

import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.TokenStream
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatService(
    private val chatSessionRepository: ChatSessionRepository,
) {
    private val chatStore: CustomChatMemoryStore = CustomChatMemoryStore(chatSessionRepository)

    fun chatStream(id: String?, message : String, model : StreamingChatModel) : TokenStream {
        val id = id ?: UUID.randomUUID().toString()

        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(100)
                .chatMemoryStore(chatStore)
                .build()
        }

        val assistant = AiServices.builder(BasicStreamAssistant::class.java)
            .streamingChatModel(model)
            .chatMemoryProvider(chatMemoryProvider)
            .build()

        val tokenStream = assistant.chat(id, message)
        return tokenStream
    }

    fun addNewChatSession(userId : String) : ChatSessionEntity{
        val newSession = ChatSessionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
        )
        chatSessionRepository.save(newSession)
        return newSession
    }

}
