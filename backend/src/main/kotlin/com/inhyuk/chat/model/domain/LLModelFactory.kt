package com.inhyuk.chat.model.domain

import com.inhyuk.chat.provider.domain.AiProvider
import com.inhyuk.chat.provider.domain.ModelProvider
import com.inhyuk.chat.provider.domain.AiProviderRepository
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
import org.springframework.stereotype.Component

@Component
class LLModelFactory(
    private val providerRepository: AiProviderRepository
) {
    fun chatModel(modelEntity: LLModelEntity): ChatModel {
        val provider = getProvider(modelEntity.providerId)
        return when (provider.provider) {
            ModelProvider.ANTHROPIC -> AnthropicChatModelFactory.chatModel(modelEntity, provider)
            ModelProvider.OPENAI_COMPATIBLE -> OpenAICompatibleChatModelFactory.chatModel(modelEntity, provider)
            ModelProvider.OPENAI -> OpenAIChatModelFactory.chatModel(modelEntity, provider)
            ModelProvider.GOOGLE -> GoogleChatModelFactory.chatModel(modelEntity, provider)
        }
    }

    fun streamChatModel(modelEntity: LLModelEntity): StreamingChatModel {
        val provider = getProvider(modelEntity.providerId)
        return when (provider.provider) {
            ModelProvider.ANTHROPIC -> AnthropicChatModelFactory.streamingChatModel(modelEntity, provider)
            ModelProvider.OPENAI_COMPATIBLE -> OpenAICompatibleChatModelFactory.streamingChatModel(
                modelEntity,
                provider
            )

            ModelProvider.OPENAI -> OpenAIChatModelFactory.streamingChatModel(modelEntity, provider)
            ModelProvider.GOOGLE -> GoogleChatModelFactory.streamingChatModel(modelEntity, provider)
        }
    }

    private fun getProvider(id: String): AiProvider {
        return providerRepository.findById(id).orElseThrow { IllegalArgumentException("Provider not found: $id") }
    }
}

private object OpenAIChatModelFactory : ChatModelFactory{
    override fun streamingChatModel(modelEntity : LLModelEntity, provider: AiProvider) : StreamingChatModel {
        val model = OpenAiStreamingChatModel.builder()
            .apiKey(provider.apiKey)
            .modelName(modelEntity.originName)
            .returnThinking(true)
            .build()
        return model
    }

    override fun chatModel(modelEntity: LLModelEntity, provider: AiProvider): ChatModel {
        val model = OpenAiChatModel.builder()
            .apiKey(provider.apiKey)
            .modelName(modelEntity.originName)
            .returnThinking(true)
            .build()
        return model
    }
}

private object OpenAICompatibleChatModelFactory : ChatModelFactory {
    override fun streamingChatModel(modelEntity : LLModelEntity, provider: AiProvider) : StreamingChatModel{
        val httpClientBuilder = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)

        val jdkHttpClientBuilder = JdkHttpClient.builder()
            .httpClientBuilder(httpClientBuilder)
        val model = OpenAiStreamingChatModel.builder()
            .apiKey(provider.apiKey)
            .modelName(modelEntity.originName)
            .httpClientBuilder(jdkHttpClientBuilder)
            .baseUrl(provider.baseUrl)
            .returnThinking(true)
            .build()
        return model
    }

    override fun chatModel(modelEntity: LLModelEntity, provider: AiProvider): ChatModel {
        val httpClientBuilder = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)

        val jdkHttpClientBuilder = JdkHttpClient.builder()
            .httpClientBuilder(httpClientBuilder)

        val model = OpenAiChatModel.builder()
            .apiKey(provider.apiKey)
            .modelName(modelEntity.originName)
            .httpClientBuilder(jdkHttpClientBuilder)
            .baseUrl(provider.baseUrl)
            .returnThinking(true)
            .build()
        return model
    }
}

private object GoogleChatModelFactory : ChatModelFactory{
    override fun streamingChatModel(modelEntity: LLModelEntity, provider: AiProvider): StreamingChatModel {
        return GoogleAiGeminiStreamingChatModel.builder()
            .modelName(modelEntity.originName)
            .apiKey(provider.apiKey)
            .returnThinking(true)
            .build()
    }

    override fun chatModel(modelEntity: LLModelEntity, provider: AiProvider): ChatModel {
        return GoogleAiGeminiChatModel.builder()
            .apiKey(provider.apiKey)
            .modelName(modelEntity.originName)
            .returnThinking(true)
            .build()
    }
}

private object AnthropicChatModelFactory : ChatModelFactory{
    override fun streamingChatModel(modelEntity: LLModelEntity, provider: AiProvider): StreamingChatModel {
        return AnthropicStreamingChatModel.builder()
            .apiKey(provider.apiKey)
            .modelName(modelEntity.originName)
            .returnThinking(true)
            .build()
    }

    override fun chatModel(modelEntity: LLModelEntity, provider: AiProvider): ChatModel {
        return AnthropicChatModel.builder()
            .apiKey(provider.apiKey)
            .modelName(modelEntity.originName)
            .returnThinking(true)
            .build()
    }

}