package com.inhyuk.chat.provider.domain

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LlmProviderService(
    private val providerRepository: LlmProviderRepository
) {
    fun create(
        name: String,
        type: ProviderType,
        status: ProviderStatus,
        baseUrl: String?,
        apiKey: String,
        extraConfig: Map<String, Any>?
    ): LlmProviderEntity {
        if (providerRepository.existsByName(name)) {
            throw IllegalArgumentException("Provider name already exists")
        }
        validateForType(type, baseUrl, apiKey)
        val entity = LlmProviderEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            status = status,
            baseUrl = baseUrl,
            apiKey = apiKey,
            extraConfig = extraConfig
        )
        return providerRepository.save(entity)
    }

    fun get(id: String): LlmProviderEntity {
        return providerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Provider not found") }
    }

    fun list(): List<LlmProviderEntity> {
        return providerRepository.findAll()
    }

    fun update(
        id: String,
        name: String,
        type: ProviderType,
        status: ProviderStatus,
        baseUrl: String?,
        apiKey: String,
        extraConfig: Map<String, Any>?
    ): LlmProviderEntity {
        val entity = get(id)
        if (providerRepository.existsByNameAndIdNot(name, id)) {
            throw IllegalArgumentException("Provider name already exists")
        }
        validateForType(type, baseUrl, apiKey)
        entity.name = name
        entity.type = type
        entity.status = status
        entity.baseUrl = baseUrl
        entity.apiKey = apiKey
        entity.extraConfig = extraConfig
        return providerRepository.save(entity)
    }

    fun updateStatus(id: String, status: ProviderStatus): LlmProviderEntity {
        val entity = get(id)
        entity.status = status
        return providerRepository.save(entity)
    }

    fun delete(id: String) {
        if (!providerRepository.existsById(id)) {
            throw IllegalArgumentException("Provider not found")
        }
        providerRepository.deleteById(id)
    }

    private fun validateForType(type: ProviderType, baseUrl: String?, apiKey: String?) {
        if (apiKey.isNullOrBlank()) {
            throw IllegalArgumentException("API key is required")
        }
        if (type == ProviderType.OPENAI_COMPATIBLE && baseUrl.isNullOrBlank()) {
            throw IllegalArgumentException("Base URL is required for OpenAI-compatible providers")
        }
    }
}
