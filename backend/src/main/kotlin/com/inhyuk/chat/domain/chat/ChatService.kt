package com.inhyuk.chat.domain.chat

import com.inhyuk.chat.domain.chat.assistant.BasicStreamAssistant
import com.inhyuk.chat.domain.chat.model.ActiveTokenStream
import com.inhyuk.chat.domain.chat.model.ChatSession
import com.inhyuk.chat.domain.chat.model.ChatSessionEntity
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.service.AiServices
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatService(
    private val sessionProvider: ChatSessionProvider,
) {

    fun chatStream(chatSession: ChatSession, message : String, model : StreamingChatModel) : ActiveTokenStream {
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

        val tokenStream = assistant.chat(id, message)

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
