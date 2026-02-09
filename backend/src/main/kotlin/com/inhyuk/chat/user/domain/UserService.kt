package com.inhyuk.chat.user.domain

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
        val roles = if (userRepository.count() == 0L) {
            setOf(Role.USER, Role.ADMIN)
        } else {
            setOf(Role.USER)
        }
        val entity = UserEntity(
            id = UUID.randomUUID().toString(),
            username = username,
            password = passwordEncoder.encode(rawPassword),
            roles = Role.toStored(roles)
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
        requesterId: String,
        requesterRoles: Set<Role>,
        newPassword: String?,
        newRoles: Set<Role>?
    ): UserEntity {
        val entity = getUser(id)
        val isAdmin = requesterRoles.contains(Role.ADMIN)
        if (!isAdmin && requesterId != id) {
            throw AccessDeniedException("Access denied")
        }
        if (!newPassword.isNullOrBlank()) {
            entity.password = passwordEncoder.encode(newPassword)
        }
        if (newRoles != null) {
            if (!isAdmin) {
                throw AccessDeniedException("Only admin can change roles")
            }
            entity.roles = Role.toStored(newRoles)
        }
        return userRepository.save(entity)
    }

    fun deleteUser(id: String, requesterId: String, requesterRoles: Set<Role>) {
        val isAdmin = requesterRoles.contains(Role.ADMIN)
        if (!isAdmin && requesterId != id) {
            throw AccessDeniedException("Access denied")
        }
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User not found")
        }
        userRepository.deleteById(id)
    }
}
