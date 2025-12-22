package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.domain.UserEntity
import com.inhyuk.chat.user.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class AuthUsecase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    /**
     * 사용자 등록; 고유한 사용자명 강제
     */
    @Transactional
    fun register(username: String, rawPassword: String): UserEntity {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }
        val entity = UserEntity(
            id = UUID.randomUUID().toString(),
            username = username,
            password = passwordEncoder.encode(rawPassword),
            roles = "ROLE_USER"
        )
        return userRepository.save(entity)
    }

    /**
     * 사용자 인증; 유효한 자격 증명 강제
     */
    fun login(username: String, rawPassword: String): UserEntity {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("Invalid credentials")
        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        return user
    }
}