package com.inhyuk.chat.user.security

import com.inhyuk.chat.user.domain.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserDetails(
    val id: String,
    private val usernameValue: String,
    private val passwordValue: String,
    private val role: Role
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return if(role == Role.ADMIN) mutableListOf(SimpleGrantedAuthority(Role.ADMIN.authority), SimpleGrantedAuthority(Role.USER.authority))
        else mutableListOf(SimpleGrantedAuthority(Role.USER.authority))
    }

    fun isAdmin(): Boolean = role == Role.ADMIN

    override fun getPassword(): String = passwordValue

    override fun getUsername(): String = usernameValue

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
