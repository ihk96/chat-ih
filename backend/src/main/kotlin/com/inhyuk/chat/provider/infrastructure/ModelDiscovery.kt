package com.inhyuk.chat.provider.infrastructure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.inhyuk.chat.provider.domain.AiProviderEntity
import com.inhyuk.chat.provider.domain.ModelProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class ModelDiscovery(
    private val httpClient: HttpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()
) {

    private val logger = LoggerFactory.getLogger(ModelDiscovery::class.java)
    private val mapper = jacksonObjectMapper()

    fun getAvailableModels(provider: AiProviderEntity): List<String> {
        return when (provider.provider) {
            ModelProvider.OPENAI, ModelProvider.OPENAI_COMPATIBLE -> fetchOpenAIModels(provider)
            ModelProvider.GOOGLE -> emptyList() // Not supported yet
            ModelProvider.ANTHROPIC -> emptyList() // Not supported yet
        }
    }

    private fun fetchOpenAIModels(connection: AiProviderEntity): List<String> {
        try {
            // Adjust baseUrl if it ends with /v1
            var baseUrl = connection.baseUrl ?: ""

            if(connection.provider == ModelProvider.OPENAI){
                baseUrl = "https://api.openai.com/v1"
            }
            // Throws on blank OpenAI Compatible base URL
            if(connection.provider == ModelProvider.OPENAI_COMPATIBLE && baseUrl.isBlank()){
                logger.error("OpenAI Compatible connection must have baseUrl set")
                throw IllegalArgumentException(
                    "OpenAI Compatible connection must have baseUrl set"
                )
            }

            val targetUrl = "$baseUrl/models"

            val request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .header("Authorization", "Bearer ${connection.apiKey}")
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) {
                logger.error("Failed to fetch models from $targetUrl: Code ${response.statusCode()}, Body: ${response.body()}")
                return emptyList()
            }

            val modelResponse : OpenAIModelResponse = mapper.readValue(response.body())
            return modelResponse.data.map { it.id }

        } catch (e: Exception) {
            logger.error("Error fetching models for connection ${connection.name}", e)
            throw RuntimeException("Failed to fetch models: ${e.message}")
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class OpenAIModelResponse(
        val data: List<OpenAIModelData>
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class OpenAIModelData(
        val id: String
    )
}