package com.inhyuk.chat.provider.api.controller.dto

import com.inhyuk.chat.provider.domain.AiProviderEntity
import com.inhyuk.chat.provider.domain.ModelProvider

data class AiProviderRequestDto(
    val name: String,
    val provider: ModelProvider,
    val baseUrl: String?,
    val apiKey: String?
) {
    fun toEntity(): AiProviderEntity {
        return AiProviderEntity(
            name = name,
            provider = provider,
            baseUrl = baseUrl,
            apiKey = apiKey
        )
    }
}

data class AiProviderResponseDto(
    val id: String?,
    val name: String,
    val provider: ModelProvider,
    val baseUrl: String?,
    // apiKey is intentionally excluded from response for security
) {
    constructor(entity: AiProviderEntity) : this(
        id = entity.id,
        name = entity.name,
        provider = entity.provider,
        baseUrl = entity.baseUrl
    )
}
