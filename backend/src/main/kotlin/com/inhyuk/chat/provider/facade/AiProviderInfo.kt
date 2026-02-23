package com.inhyuk.chat.provider.facade

import com.inhyuk.chat.provider.domain.ProviderType

data class AiProviderInfo(
    val id: String,
    val type: ProviderType,
    val apiKey: String,
    val baseUrl: String?
)
