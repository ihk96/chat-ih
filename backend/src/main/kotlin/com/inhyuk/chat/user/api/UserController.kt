package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.api.dto.SignupRequest
import com.inhyuk.chat.user.api.dto.UpdateUserRequest
import com.inhyuk.chat.user.api.dto.UserResponse
import com.inhyuk.chat.user.domain.Role
import com.inhyuk.chat.user.domain.UserService
import com.inhyuk.chat.user.security.CustomUserDetails
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<UserResponse> {
        val user = userService.register(request.username, request.password)
        return ResponseEntity.ok(UserResponse.from(user))
    }

    @GetMapping("/me")
    fun me(authentication: Authentication): ResponseEntity<UserResponse> {
        val userDetails = authentication.principal as CustomUserDetails
        val user = userService.getUser(userDetails.id)
        return ResponseEntity.ok(UserResponse.from(user))
    }
}
