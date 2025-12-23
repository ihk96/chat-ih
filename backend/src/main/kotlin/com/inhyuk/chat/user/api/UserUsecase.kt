package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.api.dto.UserResponse
import com.inhyuk.chat.user.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class UserUsecase(
    private val userRepository: UserRepository
) {

    fun getMe(id: String): UserResponse {
        val user = userRepository.findById(id).orElseThrow { IllegalArgumentException("User not found") }
        return user.let { UserResponse(it.id!!, it.username, it.roles) }
    }
}