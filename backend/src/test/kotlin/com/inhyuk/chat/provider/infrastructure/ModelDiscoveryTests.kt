package com.inhyuk.chat.provider.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.provider.domain.AiProviderEntity
import com.inhyuk.chat.provider.domain.ModelProvider
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.net.http.HttpClient
import java.net.http.HttpResponse

class ModelDiscoveryTests : BehaviorSpec({
    val httpClient = mockk<HttpClient>()
    val discoveryService = ModelDiscovery(httpClient)
    val mapper = jacksonObjectMapper()

    Given("ModelDiscoveryService") {
        val provider = AiProviderEntity(
            id = "provider-1",
            name = "OpenAI",
            provider = ModelProvider.OPENAI,
            baseUrl = null,
            apiKey = "sk-test"
        )

        When("OpenAI 모델 목록을 요청할 때") {
            val mockResponse = mockk<HttpResponse<String>>()
            val responseBody = """
                {
                  "data": [
                    { "id": "gpt-3.5-turbo" },
                    { "id": "gpt-4" }
                  ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns responseBody
            every { httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockResponse

            val result = discoveryService.getAvailableModels(provider)

            Then("모델 ID 목록을 반환해야 한다") {
                result shouldHaveSize 2
                result[0] shouldBe "gpt-3.5-turbo"
                result[1] shouldBe "gpt-4"
            }
        }

        When("OpenAI 호환 모델 목록을 요청할 때") {
            val compatibleProvider = AiProviderEntity(
                id = "provider-2",
                name = "Ollama",
                provider = ModelProvider.OPENAI_COMPATIBLE,
                baseUrl = "http://localhost:11434/v1",
                apiKey = "ollama"
            )
            val mockResponse = mockk<HttpResponse<String>>()
            val responseBody = """
                {
                  "data": [
                    { "id": "llama3" }
                  ]
                }
            """.trimIndent()

            every { mockResponse.statusCode() } returns 200
            every { mockResponse.body() } returns responseBody
            every { httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockResponse

            val result = discoveryService.getAvailableModels(compatibleProvider)

            Then("모델 ID 목록을 반환해야 한다") {
                result shouldHaveSize 1
                result[0] shouldBe "llama3"
            }
        }

        When("API 응답이 200이 아닐 때") {
            val mockResponse = mockk<HttpResponse<String>>()
            every { mockResponse.statusCode() } returns 500
            every { mockResponse.body() } returns "Internal Server Error"
            every { httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockResponse

            val result = discoveryService.getAvailableModels(provider)

            Then("빈 목록을 반환해야 한다") {
                result shouldHaveSize 0
            }
        }
    }
})
