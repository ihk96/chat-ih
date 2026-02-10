package com.inhyuk.chat.model.api

import com.inhyuk.chat.model.api.dto.ModelCatalogResponse
import com.inhyuk.chat.model.domain.LlmModelService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/models")
class ModelController(
    private val modelService: LlmModelService
) {
    @GetMapping
    fun listAvailable(): ResponseEntity<List<ModelCatalogResponse>> {
        val models = modelService.listAvailable().map { ModelCatalogResponse.from(it) }
        return ResponseEntity.ok(models)
    }
}
