package com.inhyuk.chat.user.domain

enum class Role(val authority: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

}
