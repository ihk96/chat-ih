package com.inhyuk.chat.provider.domain

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

@Service
class ProviderModelCatalogService(
    private val providerRepository: LlmProviderRepository
) {
    private val httpClient = HttpClient.newBuilder().build()
    private val mapper = jacksonObjectMapper()

    fun listModels(providerId: String): List<String> {
        val provider = providerRepository.findById(providerId)
            .orElseThrow { IllegalArgumentException("Provider not found") }
        return when (provider.type) {
            ProviderType.OPENAI -> listOpenAiModels(provider)
            ProviderType.OPENAI_COMPATIBLE -> listOpenAiCompatibleModels(provider)
            ProviderType.ANTHROPIC -> listAnthropicModels(provider)
            ProviderType.GOOGLE -> listGoogleModels(provider)
        }
    }

    private fun listOpenAiModels(provider: LlmProviderEntity): List<String> {
        val url = buildUrl(provider.baseUrl, "https://api.openai.com", "/v1/models")
        val headers = mapOf("Authorization" to "Bearer ${provider.apiKey}")
        val json = getJson(url, headers)
        return parseDataIds(json)
    }

    private fun listOpenAiCompatibleModels(provider: LlmProviderEntity): List<String> {
        val baseUrl = provider.baseUrl?.takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("Base URL is required for OpenAI-compatible providers")
        val url = buildUrl(baseUrl, baseUrl, "/v1/models")
        val headers = mapOf("Authorization" to "Bearer ${provider.apiKey}")
        val json = getJson(url, headers)
        return parseDataIds(json)
    }

    private fun listAnthropicModels(provider: LlmProviderEntity): List<String> {
        val url = buildUrl(provider.baseUrl, "https://api.anthropic.com", "/v1/models")
        val apiVersion = getStringConfig(provider.extraConfig, "apiVersion") ?: "2023-06-01"
        val headers = mapOf(
            "x-api-key" to provider.apiKey,
            "anthropic-version" to apiVersion
        )
        val json = getJson(url, headers)
        return parseDataIds(json)
    }

    private fun listGoogleModels(provider: LlmProviderEntity): List<String> {
        val baseUrl = provider.baseUrl ?: "https://generativelanguage.googleapis.com"
        val apiVersion = getStringConfig(provider.extraConfig, "apiVersion") ?: "v1beta"
        val encodedKey = URLEncoder.encode(provider.apiKey, StandardCharsets.UTF_8)
        val url = buildUrl(baseUrl, baseUrl, "/$apiVersion/models?key=$encodedKey")
        val json = getJson(url, emptyMap())
        val modelsNode = json.get("models") ?: return emptyList()
        return modelsNode.mapNotNull { it.get("name")?.asText() }
    }

    private fun buildUrl(baseUrl: String?, defaultBase: String, path: String): String {
        val base = (baseUrl ?: defaultBase).trimEnd('/')
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        if (base.endsWith("/v1") && normalizedPath.startsWith("/v1/")) {
            return base + normalizedPath.removePrefix("/v1")
        }
        return base + normalizedPath
    }

    private fun getJson(url: String, headers: Map<String, String>): JsonNode {
        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
        headers.forEach { (key, value) -> requestBuilder.header(key, value) }
        val request = requestBuilder.build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw IllegalArgumentException("Failed to fetch models: ${response.statusCode()}")
        }
        return mapper.readTree(response.body())
    }

    private fun parseDataIds(json: JsonNode): List<String> {
        val dataNode = json.get("data") ?: return emptyList()
        return dataNode.mapNotNull { it.get("id")?.asText() }
    }

    private fun getStringConfig(extraConfig: Map<String, Any>?, key: String): String? {
        val value = extraConfig?.get(key) ?: return null
        return value.toString()
    }
}
