package com.inhyuk.chat.user.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.user.api.dto.AuthRequestDto
import com.inhyuk.chat.chat.api.dto.TokenResponseDto
import com.inhyuk.chat.user.domain.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@RequestBody req: AuthRequestDto): RestResponse<TokenResponseDto> {
        val token = userService.register(req.username, req.password)
        return RestResponse.ok(TokenResponseDto(token))
    }

    @PostMapping("/login")
    fun login(@RequestBody req: AuthRequestDto): RestResponse<TokenResponseDto> {
        val token = userService.login(req.username, req.password)
        return RestResponse.ok(TokenResponseDto(token))
    }
}