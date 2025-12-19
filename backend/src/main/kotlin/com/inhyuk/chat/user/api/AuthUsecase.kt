package com.inhyuk.chat.user.api

import com.inhyuk.chat.common.config.JwtTokenProvider
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
    private val jwtTokenProvider: JwtTokenProvider
) {
    /**
     * 사용자 등록; 인증 토큰 반환; 고유한 사용자명 강제
     */
    @Transactional
    fun register(username: String, rawPassword: String): String {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }
        val entity = UserEntity(
            id = UUID.randomUUID().toString(),
            username = username,
            password = passwordEncoder.encode(rawPassword),
            roles = "ROLE_USER"
        )
        userRepository.save(entity)
        return jwtTokenProvider.generateToken(entity.id, entity.username, entity.roles)
    }

    /**
     * 사용자 인증; 인증 토큰 반환; 유효한 자격 증명 강제
     */
    fun login(username: String, rawPassword: String): String {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("Invalid credentials")
        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        return jwtTokenProvider.generateToken(user.id, user.username, user.roles)
    }
}