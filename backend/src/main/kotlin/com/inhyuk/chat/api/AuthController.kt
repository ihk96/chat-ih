package com.inhyuk.chat.api

import com.inhyuk.chat.api.dto.AuthRequestDto
import com.inhyuk.chat.api.dto.TokenResponseDto
import com.inhyuk.chat.domain.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@RequestBody req: AuthRequestDto): ResponseEntity<TokenResponseDto> {
        val token = userService.register(req.username, req.password)
        return ResponseEntity.ok(TokenResponseDto(token))
    }

    @PostMapping("/login")
    fun login(@RequestBody req: AuthRequestDto): ResponseEntity<TokenResponseDto> {
        val token = userService.login(req.username, req.password)
        return ResponseEntity.ok(TokenResponseDto(token))
    }
}
