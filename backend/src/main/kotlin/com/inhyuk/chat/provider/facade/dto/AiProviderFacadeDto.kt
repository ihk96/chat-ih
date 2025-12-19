package com.inhyuk.chat.provider.facade.dto

import com.inhyuk.chat.provider.domain.AiProviderEntity
import com.inhyuk.chat.provider.domain.ModelProvider

data class AiProviderDTO(
    val id: String?,
    val name: String,
    val provider: ModelProvider,
    val baseUrl: String?,
    val apiKey: String?
){
    constructor(entity: AiProviderEntity) : this (
        id = entity.id,
        name = entity.name,
        provider = entity.provider,
        baseUrl = entity.baseUrl,
        apiKey = entity.apiKey
    )
}