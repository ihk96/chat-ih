package com.inhyuk.chat.chat.api

import com.inhyuk.chat.chat.api.dto.ChatCompletionRequest
import com.inhyuk.chat.chat.api.dto.ChatCompletionResponse
import com.inhyuk.chat.chat.domain.LlmChatService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
