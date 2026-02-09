package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.api.dto.LoginRequest
import com.inhyuk.chat.user.api.dto.UserResponse
import com.inhyuk.chat.user.domain.UserService
import com.inhyuk.chat.user.security.CustomUserDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService
) {
    private val securityContextRepository: SecurityContextRepository = HttpSessionSecurityContextRepository()

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<UserResponse> {
        val authToken = UsernamePasswordAuthenticationToken(request.username, request.password)
        val authentication = authenticationManager.authenticate(authToken)
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        securityContextRepository.saveContext(context, httpRequest, httpResponse)
        val userDetails = authentication.principal as CustomUserDetails
        val user = userService.getUser(userDetails.id)
        return ResponseEntity.ok(UserResponse.from(user))
    }

    @PostMapping("/logout")
    fun logout(
        authentication: Authentication?,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<Void> {
        SecurityContextLogoutHandler().logout(httpRequest, httpResponse, authentication)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/me")
    fun me(authentication: Authentication): ResponseEntity<UserResponse> {
        val userDetails = authentication.principal as CustomUserDetails
        val user = userService.getUser(userDetails.id)
        return ResponseEntity.ok(UserResponse.from(user))
    }
}
