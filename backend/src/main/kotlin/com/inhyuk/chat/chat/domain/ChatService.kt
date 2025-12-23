package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.domain.assistant.BasicStreamAssistant
import com.inhyuk.chat.chat.domain.model.ActiveTokenStream
import com.inhyuk.chat.chat.domain.model.ChatSession
import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
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
            messages = ""
        )
        sessionProvider.saveSession(newSession)
        return newSession
    }

}
