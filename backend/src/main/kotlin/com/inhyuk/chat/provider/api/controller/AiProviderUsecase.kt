package com.inhyuk.chat.provider.api.controller

import com.inhyuk.chat.provider.api.controller.dto.AiProviderRequestDto
import com.inhyuk.chat.provider.api.controller.dto.AiProviderResponseDto
import com.inhyuk.chat.provider.domain.ModelDiscoveryService
import com.inhyuk.chat.provider.domain.AiProviderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AiProviderUsecase(
    private val providerRepository: AiProviderRepository,
    private val discoveryService: ModelDiscoveryService
) {

    fun getProviders(): List<AiProviderResponseDto> {
        return providerRepository.findAll().map { AiProviderResponseDto(it) }
    }

    fun getAvailableModels(providerId: String): List<String> {
        val provider = providerRepository.findById(providerId).orElseThrow { IllegalArgumentException("Provider not found") }
        return discoveryService.getAvailableModels(provider)
    }

    @Transactional
    fun createProvider(request: AiProviderRequestDto): AiProviderResponseDto {
        val entity = request.toEntity()
        return AiProviderResponseDto(providerRepository.save(entity))
    }

    fun getProvider(id: String): AiProviderResponseDto {
        val provider = providerRepository.findById(id).orElseThrow { IllegalArgumentException("Provider not found") }
        return AiProviderResponseDto(provider)
    }
}