package com.inhyuk.chat.user.domain

import com.inhyuk.chat.user.security.CustomUserDetails
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun register(username: String, rawPassword: String): UserEntity {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }
        val role = if (userRepository.count() == 0L) Role.ADMIN else Role.USER
        val entity = UserEntity(
            id = UUID.randomUUID().toString(),
            username = username,
            password = passwordEncoder.encode(rawPassword),
            role = role
        )
        return userRepository.save(entity)
    }

    fun getUser(id: String): UserEntity {
        return userRepository.findById(id).orElseThrow { IllegalArgumentException("User not found") }
    }

    fun listUsers(): List<UserEntity> {
        return userRepository.findAll()
    }

    fun updateUser(
        id: String,
        requesterUser : CustomUserDetails,
        newPassword: String?,
        newRole: Role?
    ): UserEntity {
        val entity = getUser(id)
        if (!requesterUser.isAdmin()) {
            throw AccessDeniedException("Access denied")
        }
        if (!newPassword.isNullOrBlank()) {
            entity.password = passwordEncoder.encode(newPassword)
        }
        if (newRole != null) {
            if (!requesterUser.isAdmin()) {
                throw AccessDeniedException("Only admin can change roles")
            }
            entity.role = newRole
        }
        return userRepository.save(entity)
    }

    fun deleteUser(id: String, requesterUser : CustomUserDetails) {
        if (!requesterUser.isAdmin()) {
            throw AccessDeniedException("Access denied")
        }
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User not found")
        }
        userRepository.deleteById(id)
    }
}
