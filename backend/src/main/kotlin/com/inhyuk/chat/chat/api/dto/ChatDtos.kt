package com.inhyuk.chat.chat.api.dto

import jakarta.validation.constraints.NotBlank

data class ChatCompletionRequest(
    @field:NotBlank(message = "Model id cannot be blank")
    val modelId: String,

    @field:NotBlank(message = "Message cannot be blank")
    val message: String
)

data class ChatCompletionResponse(
    val modelId: String,
    val content: String
)

data class ChatStreamDelta(
    val content: String
)

data class ChatStreamThinking(
    val content: String
)

data class ChatStreamComplete(
    val modelId: String,
    val content: String
)

data class ChatStreamError(
    val message: String
)
