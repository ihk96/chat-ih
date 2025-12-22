package com.inhyuk.chat.model.api

import com.inhyuk.chat.model.api.dto.ModelWithProviderResponseDto
import com.inhyuk.chat.model.domain.LLModelRepository
import com.inhyuk.chat.provider.facade.AiProviderFacade
import org.springframework.stereotype.Service

@Service
class BasicLLModelUsecase(
    private val llModelRepository: LLModelRepository,
    private val aiProviderFacade: AiProviderFacade
){

    fun getModels() : List<ModelWithProviderResponseDto> {
        val models = llModelRepository.findAll()
        val providers = aiProviderFacade.getProvidersInIds(models.map { it.providerId }.distinct())
        val providerMap = providers.associateBy { it.id }

        return models.map { ModelWithProviderResponseDto(
            id = it.id,
            modelName = it.publicName,
            providerId = it.providerId,
            providerName = providerMap[it.providerId]?.name ?: ""
        ) }
    }

    fun getModel(modelId: String) : ModelWithProviderResponseDto {
        val model = llModelRepository.findById(modelId).orElseThrow { IllegalArgumentException("Model not found") }
        val provider = aiProviderFacade.getProviderById(model.providerId) ?: throw IllegalArgumentException("Provider not found")

        return ModelWithProviderResponseDto(
            id = model.id,
            modelName = model.publicName,
            providerId = model.providerId,
            providerName = provider.name
        )
    }
}