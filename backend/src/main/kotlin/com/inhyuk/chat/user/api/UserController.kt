package com.inhyuk.chat.user.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.user.api.dto.UserResponse
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userUsecase: UserUsecase
) {

    @GetMapping("/me")
    fun getMe(authentication: Authentication): RestResponse<UserResponse> {
        val id = authentication.principal as? String ?: throw IllegalArgumentException("Invalid authentication principal")
        return RestResponse.ok(userUsecase.getMe(id))
    }
}