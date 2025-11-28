package com.inhyuk.chat.domain.chat

import com.inhyuk.chat.domain.chat.CustomChatMemoryStore
import dev.langchain4j.http.client.jdk.JdkHttpClient
import dev.langchain4j.mcp.client.DefaultMcpClient
import dev.langchain4j.mcp.client.McpClient
import dev.langchain4j.mcp.client.transport.McpTransport
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.TokenStream
import org.springframework.stereotype.Service
import java.net.http.HttpClient
import java.util.UUID

@Service
class ChatService(
    private val chatStore: CustomChatMemoryStore = CustomChatMemoryStore(),
) {
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

}