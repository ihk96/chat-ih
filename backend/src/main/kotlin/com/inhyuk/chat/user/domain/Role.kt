package com.inhyuk.chat.user.domain

enum class Role(val authority: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    companion object {
        fun fromAuthorities(authorities: Collection<String>): Set<Role> {
            return entries.filter { authorities.contains(it.authority) }.toSet()
        }

        fun fromStored(value: String): Set<Role> {
            if (value.isBlank()) {
                return emptySet()
            }
            val parts = value.split(",").map { it.trim() }.filter { it.isNotBlank() }
            return fromAuthorities(parts)
        }

        fun toStored(roles: Set<Role>): String {
            return roles.joinToString(",") { it.authority }
        }
    }
}
