package com.inhyuk.chat.provider.api.dto

import com.inhyuk.chat.provider.domain.LlmProviderEntity
import com.inhyuk.chat.provider.domain.ProviderStatus
import com.inhyuk.chat.provider.domain.ProviderType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateProviderRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    val name: String,

    @field:NotNull(message = "Type is required")
    val type: ProviderType,

    val status: ProviderStatus? = null,

    val baseUrl: String? = null,

    @field:NotBlank(message = "API key cannot be blank")
    val apiKey: String,

    val extraConfig: Map<String, Any>? = null
)

data class UpdateProviderRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    val name: String,

    @field:NotNull(message = "Type is required")
    val type: ProviderType,

    @field:NotNull(message = "Status is required")
    val status: ProviderStatus,

    val baseUrl: String? = null,

    @field:NotBlank(message = "API key cannot be blank")
    val apiKey: String,

    val extraConfig: Map<String, Any>? = null
)

data class UpdateProviderStatusRequest(
    @field:NotNull(message = "Status is required")
    val status: ProviderStatus
)

data class ProviderResponse(
    val id: String,
    val name: String,
    val type: ProviderType,
    val status: ProviderStatus,
    val baseUrl: String?,
    val extraConfig: Map<String, Any>?,
    val apiKeyMasked: String
) {
    companion object {
        fun from(entity: LlmProviderEntity): ProviderResponse {
            return ProviderResponse(
                id = entity.id,
                name = entity.name,
                type = entity.type,
                status = entity.status,
                baseUrl = entity.baseUrl,
                extraConfig = entity.extraConfig,
                apiKeyMasked = maskApiKey(entity.apiKey)
            )
        }

        private fun maskApiKey(apiKey: String): String {
            val visible = 4
            if (apiKey.isEmpty()) {
                return ""
            }
            if (apiKey.length <= visible) {
                return "*".repeat(apiKey.length)
            }
            return "*".repeat(apiKey.length - visible) + apiKey.takeLast(visible)
        }
    }
}
