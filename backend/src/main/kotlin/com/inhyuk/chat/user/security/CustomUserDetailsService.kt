package com.inhyuk.chat.user.security

import com.inhyuk.chat.user.domain.Role
import com.inhyuk.chat.user.domain.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found")
        return CustomUserDetails(
            id = user.id,
            usernameValue = user.username,
            passwordValue = user.password,
            role = user.role
        )
    }
}
