package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.api.dto.ChatCompletionResponse
import com.inhyuk.chat.model.facade.LlmModelFacade
import com.inhyuk.chat.provider.facade.AiProviderFacade
import com.inhyuk.chat.provider.domain.ProviderType
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import org.springframework.stereotype.Service

@Service
class LlmChatService(
    private val modelFacade: LlmModelFacade,
    private val providerFacade: AiProviderFacade
) {
    fun chat(modelId: String, message: String): ChatCompletionResponse {
        val model = modelFacade.getActive(modelId)
        val provider = providerFacade.getActive(model.providerId)

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
