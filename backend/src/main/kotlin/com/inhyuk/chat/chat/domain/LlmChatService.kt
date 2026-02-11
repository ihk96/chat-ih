package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.api.dto.ChatCompletionResponse
import com.inhyuk.chat.model.domain.LlmModelRepository
import com.inhyuk.chat.model.domain.ModelStatus
import com.inhyuk.chat.provider.domain.LlmProviderRepository
import com.inhyuk.chat.provider.domain.ProviderStatus
import com.inhyuk.chat.provider.domain.ProviderType
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import org.springframework.stereotype.Service

@Service
class LlmChatService(
    private val modelRepository: LlmModelRepository,
    private val providerRepository: LlmProviderRepository
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

        val output = chatModel.generate(message)

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
    ): ChatLanguageModel {
        return when (providerType) {
            ProviderType.OPENAI -> {
                val builder = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                baseUrl?.takeIf { it.isNotBlank() }?.let { builder.baseUrl(it) }
                builder.build()
            }

            ProviderType.OPENAI_COMPATIBLE -> {
                val normalizedBaseUrl = baseUrl?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("Base URL is required for OpenAI-compatible providers")
                OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(normalizedBaseUrl)
                    .modelName(modelName)
                    .build()
            }

            ProviderType.GOOGLE -> {
                GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .build()
            }

            ProviderType.ANTHROPIC -> {
                val builder = AnthropicChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                baseUrl?.takeIf { it.isNotBlank() }?.let { builder.baseUrl(it) }
                builder.build()
            }
        }
    }
}
