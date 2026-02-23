package com.inhyuk.chat.provider.facade

import com.inhyuk.chat.provider.domain.AiProviderRepository
import com.inhyuk.chat.provider.domain.ProviderStatus
import org.springframework.stereotype.Service

@Service
class AiProviderFacade(
    private val providerRepository: AiProviderRepository
) {
    fun get(id: String): AiProviderInfo {
        val provider = providerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Provider not found") }
        return AiProviderInfo(
            id = provider.id,
            type = provider.type,
            apiKey = provider.apiKey,
            baseUrl = provider.baseUrl
        )
    }

    fun getActive(id: String): AiProviderInfo {
        val provider = providerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Provider not found") }
        if (provider.status != ProviderStatus.ACTIVE) {
            throw IllegalArgumentException("Provider is inactive")
        }
        return AiProviderInfo(
            id = provider.id,
            type = provider.type,
            apiKey = provider.apiKey,
            baseUrl = provider.baseUrl
        )
    }

    fun exists(id: String): Boolean {
        return providerRepository.existsById(id)
    }

    fun listActiveIds(): List<String> {
        return providerRepository.findAllByStatus(ProviderStatus.ACTIVE)
            .map { it.id }
    }
}
