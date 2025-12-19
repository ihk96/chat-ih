package com.inhyuk.chat.provider.api.controller

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.provider.api.controller.dto.AiProviderRequestDto
import com.inhyuk.chat.provider.api.controller.dto.AiProviderResponseDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/providers")
class AiProviderController(
    private val providerUsecase: AiProviderUsecase
) {

    @GetMapping
    fun getProviders(): RestResponse<List<AiProviderResponseDto>> {
        return RestResponse.ok(providerUsecase.getProviders())
    }

    @GetMapping("/{id}/models")
    fun getAvailableModels(@PathVariable id: String): RestResponse<List<String>> {
        return RestResponse.ok(providerUsecase.getAvailableModels(id))
    }

    @PostMapping
    fun createProvider(@RequestBody request: AiProviderRequestDto): RestResponse<AiProviderResponseDto> {
        return RestResponse.ok(providerUsecase.createProvider(request))
    }
}
