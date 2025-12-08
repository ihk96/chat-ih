package com.inhyuk.chat.domain.connection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.inhyuk.chat.domain.connection.ModelProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class ModelDiscoveryService {

    private val logger = LoggerFactory.getLogger(ModelDiscoveryService::class.java)
    private val httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()
    private val mapper = jacksonObjectMapper()

    fun getAvailableModels(connection: ModelProviderConnection): List<String> {
        return when (connection.provider) {
            ModelProvider.OPENAI, ModelProvider.OPENAI_COMPATIBLE -> fetchOpenAIModels(connection)
            ModelProvider.GOOGLE -> emptyList() // Not supported yet
            ModelProvider.ANTHROPIC -> emptyList() // Not supported yet
        }
    }

    private fun fetchOpenAIModels(connection: ModelProviderConnection): List<String> {
        try {
            // Adjust baseUrl if it ends with /v1
            var baseUrl = connection.baseUrl
            if(baseUrl.isEmpty() && connection.provider == ModelProvider.OPENAI){
                baseUrl = "https://api.openai.com/v1"
            }
            if(!baseUrl.endsWith("/")){
                baseUrl += "/"
            }
            
            // e.g. https://api.openai.com/v1/models
            // connection.baseUrl is usually "https://api.openai.com/v1"
            // If user enters "http://localhost:11434", we might need to append v1?
            // Usually the convention for OpenAI compatible is they give the base v1 url.
            // Let's assume baseUrl includes /v1 if needed, or we append "models".
            // Implementation detail: standard is GET {baseUrl}/models
            
            val targetUrl = if(baseUrl.endsWith("v1/") || baseUrl.endsWith("v1")) {
                 "${baseUrl.removeSuffix("/")}/models"
            } else {
                 // Try appending v1/models if typical root without v1? 
                 // Safest is assuming user gives full base including version if required, but let's just append "models"
                 "$baseUrl/models" // Simple concatenation if user put full path
                 // Actually, better robustness: URI resolve?
                 // Let's just do simple join.
            }

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
