package com.inhyuk.chat.domain.user

import com.inhyuk.chat.config.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {

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

    fun login(username: String, rawPassword: String): String {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("Invalid credentials")
        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        return jwtTokenProvider.generateToken(user.id, user.username, user.roles)
    }
}
