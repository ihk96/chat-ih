package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.api.dto.ChatCompletionResponse
import com.inhyuk.chat.model.domain.LlmModelRepository
import com.inhyuk.chat.model.domain.ModelStatus
import com.inhyuk.chat.provider.domain.AiProviderRepository
import com.inhyuk.chat.provider.domain.ProviderStatus
import com.inhyuk.chat.provider.domain.ProviderType
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import org.springframework.stereotype.Service

@Service
class LlmChatService(
    private val modelRepository: LlmModelRepository,
    private val providerRepository: AiProviderRepository
) {
    fun chat(modelId: String, message: String): ChatCompletionResponse {
        val model = modelRepository.findById(modelId)
            .orElseThrow { IllegalArgumentException("Model not found") }

        if (model.status != ModelStatus.ACTIVE) {
            throw IllegalArgumentException("Model is inactive")
        }

        val provider = providerRepository.findById(model.providerId)
            .orElseThrow { IllegalArgumentException("Provider not found") }

        if (provider.status != ProviderStatus.ACTIVE) {
            throw IllegalArgumentException("Provider is inactive")
        }

        val chatModel = buildChatModel(
            providerType = provider.type,
            modelName = model.originName,
            apiKey = provider.apiKey,
            baseUrl = provider.baseUrl
        )


        val output = chatModel.chat(message)

        return ChatCompletionResponse(
            modelId = model.id,
            content = output
        )
    }

    private fun buildChatModel(
        providerType: ProviderType,
        modelName: String,
        apiKey: String,
        baseUrl: String?
    ): ChatModel {
        return when (providerType) {
            ProviderType.OPENAI -> {
                val builder = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .returnThinking(true)
                    .modelName(modelName)
                builder.build()
            }

            ProviderType.OPENAI_COMPATIBLE -> {
                val normalizedBaseUrl = baseUrl?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("Base URL is required for OpenAI-compatible providers")
                OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(normalizedBaseUrl)
                    .returnThinking(true)
                    .modelName(modelName)
                    .build()
            }

            ProviderType.GOOGLE -> {
                GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .returnThinking(true)
                    .sendThinking(true)
                    .modelName(modelName)
                    .build()
            }

            ProviderType.ANTHROPIC -> {
                val builder = AnthropicChatModel.builder()
                    .apiKey(apiKey)
                    .returnThinking(true)
                    .sendThinking(true)
                    .modelName(modelName)
                builder.build()
            }
        }
    }
}
