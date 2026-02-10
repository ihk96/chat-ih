package com.inhyuk.chat.model.domain

import com.inhyuk.chat.provider.domain.LlmProviderRepository
import com.inhyuk.chat.provider.domain.ProviderStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LlmModelService(
    private val modelRepository: LlmModelRepository,
    private val providerRepository: LlmProviderRepository
) {
    fun create(
        providerId: String,
        originName: String,
        publicName: String
    ): LlmModelEntity {
        if (!providerRepository.existsById(providerId)) {
            throw IllegalArgumentException("Provider not found")
        }
        val entity = LlmModelEntity(
            id = UUID.randomUUID().toString(),
            providerId = providerId,
            originName = originName,
            publicName = publicName,
            status = ModelStatus.INACTIVE,
            extraConfig = null
        )
        return modelRepository.save(entity)
    }

    fun get(id: String): LlmModelEntity {
        return modelRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Model not found") }
    }

    fun list(): List<LlmModelEntity> {
        return modelRepository.findAll()
    }

    fun listAvailable(): List<LlmModelEntity> {
        val activeProviderIds = providerRepository.findAllByStatus(ProviderStatus.ACTIVE)
            .map { it.id }
        if (activeProviderIds.isEmpty()) {
            return emptyList()
        }
        return modelRepository.findAllByStatusAndProviderIdIn(
            status = ModelStatus.ACTIVE,
            providerIds = activeProviderIds
        )
    }

    fun update(
        id: String,
        providerId: String,
        originName: String,
        publicName: String,
        status: ModelStatus,
        extraConfig: Map<String, Any>?
    ): LlmModelEntity {
        val entity = get(id)
        if (!providerRepository.existsById(providerId)) {
            throw IllegalArgumentException("Provider not found")
        }
        entity.providerId = providerId
        entity.originName = originName
        entity.publicName = publicName
        entity.status = status
        entity.extraConfig = extraConfig
        return modelRepository.save(entity)
    }

    fun updateStatus(id: String, status: ModelStatus): LlmModelEntity {
        val entity = get(id)
        entity.status = status
        return modelRepository.save(entity)
    }

    fun delete(id: String) {
        if (!modelRepository.existsById(id)) {
            throw IllegalArgumentException("Model not found")
        }
        modelRepository.deleteById(id)
    }
}
