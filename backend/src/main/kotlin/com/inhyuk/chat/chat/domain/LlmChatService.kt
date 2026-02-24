package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.api.dto.ChatCompletionResponse
import com.inhyuk.chat.chat.api.dto.ChatStreamComplete
import com.inhyuk.chat.chat.api.dto.ChatStreamDelta
import com.inhyuk.chat.chat.api.dto.ChatStreamError
import com.inhyuk.chat.chat.api.dto.ChatStreamThinking
import com.inhyuk.chat.model.facade.LlmModelFacade
import com.inhyuk.chat.provider.facade.AiProviderFacade
import com.inhyuk.chat.provider.domain.ProviderType
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.PartialThinking
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class LlmChatService(
    private val modelFacade: LlmModelFacade,
    private val providerFacade: AiProviderFacade,
    @Qualifier("chatStreamingExecutor")
    private val taskExecutor: TaskExecutor
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

    fun streamChat(modelId: String, message: String, emitter: SseEmitter) {
        val model = modelFacade.getActive(modelId)
        val provider = providerFacade.getActive(model.providerId)

        val streamingChatModel = buildStreamingChatModel(
            providerType = provider.type,
            modelName = model.originName,
            apiKey = provider.apiKey,
            baseUrl = provider.baseUrl
        )


        taskExecutor.execute {
            val aggregated = StringBuilder()
            streamingChatModel.chat(message, object : StreamingChatResponseHandler {
                override fun onPartialResponse(partialResponse: String) {
                    if (partialResponse.isEmpty()) {
                        return
                    }
                    aggregated.append(partialResponse)
                    safeSend(emitter, "delta", ChatStreamDelta(partialResponse))
                }

                override fun onPartialThinking(partialThinking: PartialThinking) {
                    val text = partialThinking.text()
                    if (text.isEmpty()) {
                        return
                    }
                    safeSend(emitter, "thinking", ChatStreamThinking(text))
                }

                override fun onCompleteResponse(response: ChatResponse) {
                    val fullContent = response.aiMessage().text().takeIf { it.isNotBlank() }
                        ?: aggregated.toString()
                    safeSend(
                        emitter,
                        "complete",
                        ChatStreamComplete(
                            modelId = model.id,
                            content = fullContent
                        )
                    )
                    emitter.complete()
                }

                override fun onError(error: Throwable) {
                    safeSend(
                        emitter,
                        "error",
                        ChatStreamError(error.message ?: "Streaming chat failed")
                    )
                    emitter.completeWithError(error)
                }
            })
        }
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

    private fun buildStreamingChatModel(
        providerType: ProviderType,
        modelName: String,
        apiKey: String,
        baseUrl: String?
    ): StreamingChatModel {
        return when (providerType) {
            ProviderType.OPENAI -> {
                OpenAiStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .returnThinking(true)
                    .modelName(modelName)
                    .build()
            }

            ProviderType.OPENAI_COMPATIBLE -> {
                val normalizedBaseUrl = baseUrl?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException("Base URL is required for OpenAI-compatible providers")
                OpenAiStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(normalizedBaseUrl)
                    .returnThinking(true)
                    .modelName(modelName)
                    .build()
            }

            ProviderType.GOOGLE -> {
                GoogleAiGeminiStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .returnThinking(true)
                    .sendThinking(true)
                    .modelName(modelName)
                    .build()
            }

            ProviderType.ANTHROPIC -> {
                AnthropicStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .returnThinking(true)
                    .sendThinking(true)
                    .modelName(modelName)
                    .build()
            }
        }
    }

    private fun safeSend(emitter: SseEmitter, eventName: String, data: Any) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data))
        } catch (ex: IOException) {
            emitter.completeWithError(ex)
        } catch (ex: IllegalStateException) {
            emitter.complete()
        }
    }
}
