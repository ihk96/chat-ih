package com.inhyuk.chat.chat.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.chat.api.dto.ChatRequestDto
import com.inhyuk.chat.chat.api.dto.UpdateSessionRequestDto
import com.inhyuk.chat.chat.api.dto.ChatSessionDto
import com.inhyuk.chat.chat.application.BasicChatUsecase
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

    @PostMapping(value = ["/v1"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun chatCompletions(@RequestBody request: ChatRequestDto, authentication: Authentication): ResponseEntity<SseEmitter?> {
        if(request.id.isNullOrEmpty()){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Session ID is required")
        }
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
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
    fun createChatSession(@RequestBody request: ChatRequestDto, authentication: Authentication): RestResponse<String> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val sessionId = chatUsecase.initSession(userId, request.message, request.model)
        return RestResponse.ok(sessionId)
    }

    @GetMapping("/v1/sessions/{sessionId}/subscribe")
    fun getSessionSubscribe(@PathVariable sessionId: String, authentication: Authentication): ResponseEntity<SseEmitter?> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return ResponseEntity.ok(chatUsecase.subscribe(userId, sessionId))
    }

    @GetMapping("/v1/sessions/{sessionId}")
    fun getSession(@PathVariable sessionId: String, authentication: Authentication): RestResponse<ChatSessionDto> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return RestResponse.ok(chatUsecase.getSession(userId,sessionId))
    }

    @GetMapping("/v1/sessions")
    fun getSessions(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): RestResponse<Page<ChatSessionDto>> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastModifiedDate"))
        return RestResponse.ok(chatUsecase.getSessions(userId, pageable))
    }

    @DeleteMapping("/v1/sessions/{sessionId}")
    fun deleteSession(@PathVariable sessionId: String, authentication: Authentication): RestResponse<Unit> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        chatUsecase.deleteSession(userId, sessionId)
        return RestResponse.ok(Unit)
    }

    @PatchMapping("/v1/sessions/{sessionId}")
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