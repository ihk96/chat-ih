package com.inhyuk.chat.user.api.dto

import com.inhyuk.chat.user.domain.Role
import com.inhyuk.chat.user.domain.UserEntity
import jakarta.validation.constraints.NotBlank

data class SignupRequest(
    @field:NotBlank(message = "Username cannot be blank")
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Username cannot be blank")
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)

data class UpdateUserRequest(
    val password: String? = null,
    val roles: Set<Role>? = null
)

data class UserResponse(
    val id: String,
    val username: String,
    val roles: Set<Role>
) {
    companion object {
        fun from(entity: UserEntity): UserResponse {
            return UserResponse(
                id = entity.id,
                username = entity.username,
                roles = Role.fromStored(entity.roles)
            )
        }
    }
}
