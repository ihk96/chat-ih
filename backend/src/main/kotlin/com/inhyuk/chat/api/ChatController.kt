package com.inhyuk.chat.api

import com.inhyuk.chat.api.dto.ChatRequestDto
import com.inhyuk.chat.usecase.BasicChatUsecase
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatUsecase : BasicChatUsecase
) {

    @PostMapping(value = ["/v1"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun chatCompletions(@RequestBody request: ChatRequestDto): ResponseEntity<SseEmitter?> {
        val emitter = chatUsecase.sse(
            id = request.id,
            message = request.message,
            modelId = request.model
        )

        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache")
            .header("X-Accel-Buffering", "no") // Nginx 버퍼링 방지
            .body<SseEmitter?>(emitter)
    }

}