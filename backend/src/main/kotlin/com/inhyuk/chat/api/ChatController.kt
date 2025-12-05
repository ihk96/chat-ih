package com.inhyuk.chat.api

import com.inhyuk.chat.api.dto.ChatRequestDto
import com.inhyuk.chat.usecase.BasicChatUsecase
import com.inhyuk.chat.usecase.dto.ChatSessionDto
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
    fun chatCompletions(@RequestBody request: ChatRequestDto, authentication: Authentication): ResponseEntity<SseEmitter?> {
        if(request.id.isNullOrEmpty()){
            return ResponseEntity.badRequest().build()
        }
        val userId = authentication.principal as? String ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val emitter = chatUsecase.chat(
            userId = userId,
            sessionId = request.id,
            message = request.message,
            modelId = request.model
        )

        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache")
            .header("X-Accel-Buffering", "no") // Nginx 버퍼링 방지
            .body<SseEmitter?>(emitter)
    }

    @PostMapping("/v1/sessions")
    fun createChatSession(@RequestBody request: ChatRequestDto, authentication: Authentication): ResponseEntity<String> {
        val userId = authentication.principal as? String ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val sessionId = chatUsecase.initSession(userId, request.message, request.model)
        return ResponseEntity.ok(sessionId)
    }

    @GetMapping("/v1/sessions/{sessionId}/subscribe")
    fun getSessionSubscribe(@PathVariable sessionId: String, authentication: Authentication): ResponseEntity<SseEmitter?> {
        val userId = authentication.principal as? String ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return ResponseEntity.ok(chatUsecase.subscribe(userId, sessionId))
    }

    @GetMapping("/v1/sessions/{sessionId}")
    fun getSession(@PathVariable sessionId: String, authentication: Authentication): ResponseEntity<ChatSessionDto> {
        val userId = authentication.principal as? String ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return ResponseEntity.ok(chatUsecase.getSession(userId,sessionId))
    }


}

