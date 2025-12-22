package com.inhyuk.chat.user.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.user.api.dto.AuthRequestDto
import com.inhyuk.chat.user.domain.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authUsecase: AuthUsecase
) {

    @PostMapping("/register")
    fun register(@RequestBody req: AuthRequestDto, request: HttpServletRequest): RestResponse<Unit> {
        val user = authUsecase.register(req.username, req.password)
        loginUser(user.id, user.roles, request)
        return RestResponse.ok(Unit)
    }

    @PostMapping("/login")
    fun login(@RequestBody req: AuthRequestDto, request: HttpServletRequest): RestResponse<Unit> {
        val user = authUsecase.login(req.username, req.password)
        loginUser(user.id, user.roles, request)
        return RestResponse.ok(Unit)
    }

    private fun loginUser(userId: String, roles: String, request: HttpServletRequest) {
        val authorities = roles.split(",").filter { it.isNotBlank() }.map { SimpleGrantedAuthority(it.trim()) }
        val authentication = UsernamePasswordAuthenticationToken(userId, null, authorities)
        val context = SecurityContextHolder.getContext()
        context.authentication = authentication

        val session = request.getSession(true)
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context)
    }
}