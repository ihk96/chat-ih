package com.inhyuk.chat.chat.api

import com.inhyuk.chat.chat.api.dto.ChatCompletionRequest
import com.inhyuk.chat.chat.api.dto.ChatCompletionResponse
import com.inhyuk.chat.chat.domain.LlmChatService
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val llmChatService: LlmChatService
) {
    @PostMapping("/completions")
    fun complete(
        @Valid @RequestBody request: ChatCompletionRequest
    ): ResponseEntity<ChatCompletionResponse> {
        val response = llmChatService.chat(request.modelId, request.message)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/completions/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(
        @Valid @RequestBody request: ChatCompletionRequest
    ): SseEmitter {
        val emitter = SseEmitter(0L)
        llmChatService.streamChat(request.modelId, request.message, emitter)
        return emitter
    }
}
