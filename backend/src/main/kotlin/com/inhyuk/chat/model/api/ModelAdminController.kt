package com.inhyuk.chat.model.api

import com.inhyuk.chat.model.api.dto.CreateModelRequest
import com.inhyuk.chat.model.api.dto.ModelResponse
import com.inhyuk.chat.model.api.dto.UpdateModelRequest
import com.inhyuk.chat.model.api.dto.UpdateModelStatusRequest
import com.inhyuk.chat.model.domain.LlmModelService
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
@RequestMapping("/api/v1/admin/models")
class ModelAdminController(
    private val modelService: LlmModelService
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateModelRequest
    ): ResponseEntity<ModelResponse> {
        val model = modelService.create(
            providerId = request.providerId,
            originName = request.originName,
            publicName = request.publicName
        )
        return ResponseEntity.ok(ModelResponse.from(model))
    }

    @GetMapping
    fun list(): ResponseEntity<List<ModelResponse>> {
        val models = modelService.list().map { ModelResponse.from(it) }
        return ResponseEntity.ok(models)
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: String
    ): ResponseEntity<ModelResponse> {
        val model = modelService.get(id)
        return ResponseEntity.ok(ModelResponse.from(model))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateModelRequest
    ): ResponseEntity<ModelResponse> {
        val model = modelService.update(
            id = id,
            providerId = request.providerId,
            originName = request.originName,
            publicName = request.publicName,
            status = request.status,
            extraConfig = request.extraConfig
        )
        return ResponseEntity.ok(ModelResponse.from(model))
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateModelStatusRequest
    ): ResponseEntity<ModelResponse> {
        val model = modelService.updateStatus(id, request.status)
        return ResponseEntity.ok(ModelResponse.from(model))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String
    ): ResponseEntity<Void> {
        modelService.delete(id)
        return ResponseEntity.ok().build()
    }
}
