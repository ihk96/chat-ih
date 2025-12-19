package com.inhyuk.chat.user.facade.dto

import com.inhyuk.chat.user.domain.UserEntity

data class UserDTO(
    val id: String,
    val username: String,
    val password: String,
    val roles: String
) {

    companion object {
        fun fromEntity(entity: UserEntity): UserDTO {
            return UserDTO(
                id = entity.id,
                username = entity.username,
                password = entity.password,
                roles = entity.roles
            )
        }
    }
}