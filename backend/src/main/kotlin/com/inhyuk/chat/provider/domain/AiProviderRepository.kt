package com.inhyuk.chat.provider.domain

import org.springframework.data.jpa.repository.JpaRepository

interface AiProviderRepository : JpaRepository<AiProviderEntity, String> {
    fun existsByName(name: String): Boolean
    fun existsByNameAndIdNot(name: String, id: String): Boolean
    fun findAllByStatus(status: ProviderStatus): List<AiProviderEntity>
}
