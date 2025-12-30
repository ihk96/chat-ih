package com.inhyuk.chat.chat.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.chat.api.dto.ChatRequestDto
import com.inhyuk.chat.chat.api.dto.UpdateSessionRequestDto
import com.inhyuk.chat.chat.api.dto.ChatSessionDto
import com.inhyuk.chat.chat.api.BasicChatUsecase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val chatUsecase : BasicChatUsecase
) {

    @PostMapping(value = ["/sessions/{sessionId}/messages"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun chatCompletions(@RequestBody request: ChatRequestDto, @PathVariable sessionId: String, authentication: Authentication): ResponseEntity<SseEmitter?> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val emitter = chatUsecase.chat(
            userId = userId,
            sessionId = sessionId,
            message = request.message,
            modelId = request.model,
            files = request.files
        )

        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache")
            .header("X-Accel-Buffering", "no") // Nginx 버퍼링 방지
            .body<SseEmitter?>(emitter)
    }

    @PostMapping("/sessions")
    fun createChatSession(@RequestBody request: ChatRequestDto, authentication: Authentication): RestResponse<String> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val sessionId = chatUsecase.initSession(userId, request.message, request.model, request.files)
        return RestResponse.ok(sessionId)
    }

    @GetMapping(value=["/sessions/{sessionId}/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getSessionSubscribe(@PathVariable sessionId: String, authentication: Authentication): ResponseEntity<SseEmitter?> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache")
            .header("X-Accel-Buffering", "no") // Nginx 버퍼링 방지
            .body<SseEmitter?>(chatUsecase.subscribe(userId, sessionId))
    }

    @GetMapping("/sessions/{sessionId}")
    fun getSession(@PathVariable sessionId: String, authentication: Authentication): RestResponse<ChatSessionDto> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return RestResponse.ok(chatUsecase.getSession(userId,sessionId))
    }

    @GetMapping("/sessions")
    fun getSessions(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): RestResponse<Page<ChatSessionDto>> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastModifiedDate"))
        return RestResponse.ok(chatUsecase.getSessions(userId, pageable))
    }

    @DeleteMapping("/sessions/{sessionId}")
    fun deleteSession(@PathVariable sessionId: String, authentication: Authentication): RestResponse<Unit> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        chatUsecase.deleteSession(userId, sessionId)
        return RestResponse.ok(Unit)
    }

    @PatchMapping("/sessions/{sessionId}")
    fun updateSession(
        @PathVariable sessionId: String,
        @RequestBody request: UpdateSessionRequestDto,
        authentication: Authentication
    ): RestResponse<Unit> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        chatUsecase.updateSessionTitle(userId, sessionId, request.title)
        return RestResponse.ok(Unit)
    }


}