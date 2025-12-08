package com.inhyuk.chat.api.user.connection.dto

import com.inhyuk.chat.domain.connection.ModelProviderConnection
import com.inhyuk.chat.domain.connection.ModelProvider

data class ConnectionRequestDto(
    val name: String,
    val provider: ModelProvider,
    val baseUrl: String,
    val apiKey: String
) {
    fun toEntity(): ModelProviderConnection {
        return ModelProviderConnection(
            name = name,
            provider = provider,
            baseUrl = baseUrl,
            apiKey = apiKey
        )
    }
}

data class ConnectionResponseDto(
    val id: String,
    val name: String,
    val provider: ModelProvider,
    val baseUrl: String,
    // apiKey is intentionally excluded from response for security
) {
    constructor(entity: ModelProviderConnection) : this(
        id = entity.id,
        name = entity.name,
        provider = entity.provider,
        baseUrl = entity.baseUrl
    )
}
