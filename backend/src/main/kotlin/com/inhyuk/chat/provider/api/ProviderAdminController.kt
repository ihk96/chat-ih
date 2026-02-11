package com.inhyuk.chat.provider.api

import com.inhyuk.chat.provider.api.dto.CreateProviderRequest
import com.inhyuk.chat.provider.api.dto.ProviderModelResponse
import com.inhyuk.chat.provider.api.dto.ProviderResponse
import com.inhyuk.chat.provider.api.dto.UpdateProviderRequest
import com.inhyuk.chat.provider.api.dto.UpdateProviderStatusRequest
import com.inhyuk.chat.provider.domain.ProviderModelCatalogService
import com.inhyuk.chat.provider.domain.AiProviderService
import com.inhyuk.chat.provider.domain.ProviderStatus
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/providers")
class ProviderAdminController(
    private val providerService: AiProviderService,
    private val providerModelCatalogService: ProviderModelCatalogService
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateProviderRequest
    ): ResponseEntity<ProviderResponse> {
        val provider = providerService.create(
            name = request.name,
            type = request.type,
            status = request.status ?: ProviderStatus.ACTIVE,
            baseUrl = request.baseUrl,
            apiKey = request.apiKey,
            extraConfig = request.extraConfig
        )
        return ResponseEntity.ok(ProviderResponse.from(provider))
    }

    @GetMapping
    fun list(): ResponseEntity<List<ProviderResponse>> {
        val providers = providerService.list().map { ProviderResponse.from(it) }
        return ResponseEntity.ok(providers)
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: String
    ): ResponseEntity<ProviderResponse> {
        val provider = providerService.get(id)
        return ResponseEntity.ok(ProviderResponse.from(provider))
    }

    @GetMapping("/{id}/models")
    fun listProviderModels(
        @PathVariable id: String
    ): ResponseEntity<List<ProviderModelResponse>> {
        val models = providerModelCatalogService.listModels(id)
        val response = models.map { ProviderModelResponse(it) }
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateProviderRequest
    ): ResponseEntity<ProviderResponse> {
        val provider = providerService.update(
            id = id,
            name = request.name,
            type = request.type,
            status = request.status,
            baseUrl = request.baseUrl,
            apiKey = request.apiKey,
            extraConfig = request.extraConfig
        )
        return ResponseEntity.ok(ProviderResponse.from(provider))
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateProviderStatusRequest
    ): ResponseEntity<ProviderResponse> {
        val provider = providerService.updateStatus(id, request.status)
        return ResponseEntity.ok(ProviderResponse.from(provider))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String
    ): ResponseEntity<Void> {
        providerService.delete(id)
        return ResponseEntity.ok().build()
    }
}
