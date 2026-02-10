package com.inhyuk.chat.provider.domain

import org.springframework.data.jpa.repository.JpaRepository

interface LlmProviderRepository : JpaRepository<LlmProviderEntity, String> {
    fun existsByName(name: String): Boolean
    fun existsByNameAndIdNot(name: String, id: String): Boolean
    fun findAllByStatus(status: ProviderStatus): List<LlmProviderEntity>
}
