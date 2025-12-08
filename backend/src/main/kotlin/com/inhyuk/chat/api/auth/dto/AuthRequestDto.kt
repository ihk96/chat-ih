package com.inhyuk.chat.api.auth.dto

data class AuthRequestDto(
    val username: String,
    val password: String,
)