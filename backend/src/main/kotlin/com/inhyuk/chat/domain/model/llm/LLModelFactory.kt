package com.inhyuk.chat.domain.model.llm

import com.inhyuk.chat.domain.model.ModelProvider
import dev.langchain4j.http.client.jdk.JdkHttpClient
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import java.net.http.HttpClient

import com.inhyuk.chat.domain.connection.ModelProviderConnection
import com.inhyuk.chat.domain.connection.ModelProviderConnectionRepository
import org.springframework.stereotype.Component

@Component
class LLModelFactory(
    private val connectionRepository: ModelProviderConnectionRepository
) {
    fun chatModel(modelEntity: LLModel): ChatModel {
        val connection = getConnection(modelEntity.connectionId)
        return when(connection.provider){
            ModelProvider.ANTHROPIC -> AnthropicChatModelFactory.chatModel(modelEntity, connection)
            ModelProvider.OPENAI_COMPATIBLE -> OpenAICompatibleChatModelFactory.chatModel(modelEntity, connection)
            ModelProvider.OPENAI -> OpenAIChatModelFactory.chatModel(modelEntity, connection)
            ModelProvider.GOOGLE -> GoogleChatModelFactory.chatModel(modelEntity, connection)
        }
    }
    fun streamChatModel(modelEntity : LLModel) : StreamingChatModel{
        val connection = getConnection(modelEntity.connectionId)
        return when(connection.provider){
            ModelProvider.ANTHROPIC -> AnthropicChatModelFactory.streamingChatModel(modelEntity, connection)
            ModelProvider.OPENAI_COMPATIBLE -> OpenAICompatibleChatModelFactory.streamingChatModel(modelEntity, connection)
            ModelProvider.OPENAI -> OpenAIChatModelFactory.streamingChatModel(modelEntity, connection)
            ModelProvider.GOOGLE -> GoogleChatModelFactory.streamingChatModel(modelEntity, connection)
        }
    }

    private fun getConnection(id: String): ModelProviderConnection {
        return connectionRepository.findById(id).orElseThrow { IllegalArgumentException("Connection not found: $id") }
    }

    }
    
    private interface ChatModelFactory {
        fun streamingChatModel(modelEntity: LLModel, connection: ModelProviderConnection): StreamingChatModel
        fun chatModel(modelEntity: LLModel, connection: ModelProviderConnection): ChatModel
    }
    
    // Internal factories could be refactored too, but for now we keep them private or make them methods.
    // Simplifying by keeping logic inline or delegating.
    // For testability, better to have them as methods we can mock or separate classes.
    // Let's keep the structure but instantiate them or access them via methods.

    private val openAIChatModelFactory = OpenAIChatModelFactory
    private val openAICompatibleChatModelFactory = OpenAICompatibleChatModelFactory
    private val googleChatModelFactory = GoogleChatModelFactory
    private val anthropicChatModelFactory = AnthropicChatModelFactory

    private object OpenAIChatModelFactory : ChatModelFactory{
        override fun streamingChatModel(modelEntity : LLModel, connection: ModelProviderConnection) : StreamingChatModel {
            val model = OpenAiStreamingChatModel.builder()
                .apiKey(connection.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
            return model
        }

        override fun chatModel(modelEntity: LLModel, connection: ModelProviderConnection): ChatModel {
            val model = OpenAiChatModel.builder()
                .apiKey(connection.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
            return model
        }
    }

    private object OpenAICompatibleChatModelFactory : ChatModelFactory {
        override fun streamingChatModel(modelEntity : LLModel, connection: ModelProviderConnection) : StreamingChatModel{
            val httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)

            val jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(httpClientBuilder)
            val model = OpenAiStreamingChatModel.builder()
                .apiKey(connection.apiKey)
                .modelName(modelEntity.originName)
                .httpClientBuilder(jdkHttpClientBuilder)
                .baseUrl(connection.baseUrl) // http://172.18.102.145:12434/v1
                .returnThinking(true)
                .build()
            return model
        }

        override fun chatModel(modelEntity: LLModel, connection: ModelProviderConnection): ChatModel {
            val httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)

            val jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(httpClientBuilder)

            val model = OpenAiChatModel.builder()
                .apiKey(connection.apiKey)
                .modelName(modelEntity.originName)
                .httpClientBuilder(jdkHttpClientBuilder)
                .baseUrl(connection.baseUrl) // http://172.18.102.145:12434/v1
                .returnThinking(true)
                .build()
            return model
        }
    }

    private object GoogleChatModelFactory : ChatModelFactory{
        override fun streamingChatModel(modelEntity: LLModel, connection: ModelProviderConnection): StreamingChatModel {
            return GoogleAiGeminiStreamingChatModel.builder()
                .modelName(modelEntity.originName)
                .apiKey(connection.apiKey)
                .returnThinking(true)
                .build()
        }

        override fun chatModel(modelEntity: LLModel, connection: ModelProviderConnection): ChatModel {
            return GoogleAiGeminiChatModel.builder()
                .apiKey(connection.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
        }
    }

    private object AnthropicChatModelFactory : ChatModelFactory{
        override fun streamingChatModel(modelEntity: LLModel, connection: ModelProviderConnection): StreamingChatModel {
            return AnthropicStreamingChatModel.builder()
                .apiKey(connection.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
        }

        override fun chatModel(modelEntity: LLModel, connection: ModelProviderConnection): ChatModel {
            return AnthropicChatModel.builder()
                .apiKey(connection.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
        }

    }

}