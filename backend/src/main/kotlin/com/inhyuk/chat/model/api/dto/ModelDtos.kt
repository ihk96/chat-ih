package com.inhyuk.chat.model.api.dto

import com.inhyuk.chat.model.domain.LlmModelEntity
import com.inhyuk.chat.model.domain.ModelStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateModelRequest(
    @field:NotBlank(message = "Provider id cannot be blank")
    val providerId: String,

    @field:NotBlank(message = "Origin name cannot be blank")
    @field:Size(min = 2, max = 100, message = "Origin name must be between 2 and 100 characters")
    val originName: String,

    @field:NotBlank(message = "Public name cannot be blank")
    @field:Size(min = 2, max = 100, message = "Public name must be between 2 and 100 characters")
    val publicName: String,

    val status: ModelStatus? = null,

    val extraConfig: Map<String, Any>? = null
)

data class UpdateModelRequest(
    @field:NotBlank(message = "Provider id cannot be blank")
    val providerId: String,

    @field:NotBlank(message = "Origin name cannot be blank")
    @field:Size(min = 2, max = 100, message = "Origin name must be between 2 and 100 characters")
    val originName: String,

    @field:NotBlank(message = "Public name cannot be blank")
    @field:Size(min = 2, max = 100, message = "Public name must be between 2 and 100 characters")
    val publicName: String,

    @field:NotNull(message = "Status is required")
    val status: ModelStatus,

    val extraConfig: Map<String, Any>? = null
)

data class UpdateModelStatusRequest(
    @field:NotNull(message = "Status is required")
    val status: ModelStatus
)

data class ModelResponse(
    val id: String,
    val providerId: String,
    val originName: String,
    val publicName: String,
    val status: ModelStatus,
    val extraConfig: Map<String, Any>?
) {
    companion object {
        fun from(entity: LlmModelEntity): ModelResponse {
            return ModelResponse(
                id = entity.id,
                providerId = entity.providerId,
                originName = entity.originName,
                publicName = entity.publicName,
                status = entity.status,
                extraConfig = entity.extraConfig
            )
        }
    }
}
