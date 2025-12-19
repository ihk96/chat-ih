package com.inhyuk.chat.provider.facade

import com.inhyuk.chat.provider.domain.AiProviderRepository
import com.inhyuk.chat.provider.facade.dto.AiProviderDTO
import org.springframework.stereotype.Component

@Component
class AiProviderFacade(
    private val aiProviderRepository: AiProviderRepository
) {

    fun getProviders() : List<AiProviderDTO> {
        return aiProviderRepository.findAll().map { AiProviderDTO(it) }
    }

    fun getProvidersInIds(ids: List<String>) : List<AiProviderDTO> {
        return aiProviderRepository.findAllById(ids).map { AiProviderDTO(it) }
    }

    fun getProviderById(id: String) : AiProviderDTO? {
        return aiProviderRepository.findById(id).map { AiProviderDTO(it) }.orElse(null)
    }
}