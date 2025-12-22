package com.inhyuk.chat.provider.api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.provider.api.controller.dto.AiProviderRequestDto
import com.inhyuk.chat.provider.api.controller.dto.AiProviderResponseDto
import com.inhyuk.chat.provider.domain.ModelProvider
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AiProviderAdminControllerTest : BehaviorSpec({
    val providerUsecase = mockk<AiProviderAdminUsecase>()
    val controller = AiProviderAdminController(providerUsecase)
    val mapper = jacksonObjectMapper()
    val mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .build()

    val providerId = "provider-1"

    Given("AiProviderController") {
        When("GET /api/v1/providers 호출 시") {
            val responseDto = AiProviderResponseDto(
                id = providerId,
                name = "OpenAI",
                provider = ModelProvider.OPENAI,
                baseUrl = "https://api.openai.com/v1"
            )
            every { providerUsecase.getProviders() } returns listOf(responseDto)

            Then("200 OK와 공급자 목록을 반환한다") {
                mockMvc.perform(get("/api/v1/providers"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data[0].id").value(providerId))
                    .andExpect(jsonPath("$.data[0].name").value("OpenAI"))
            }
        }

        When("GET /api/v1/providers/{id}/models 호출 시") {
            val models = listOf("gpt-3.5-turbo", "gpt-4")
            every { providerUsecase.getAvailableModels(providerId) } returns models

            Then("200 OK와 모델 목록을 반환한다") {
                mockMvc.perform(get("/api/v1/providers/$providerId/models"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data").isArray)
                    .andExpect(jsonPath("$.data[0]").value("gpt-3.5-turbo"))
                    .andExpect(jsonPath("$.data[1]").value("gpt-4"))
            }
        }

        When("POST /api/v1/providers 호출 시") {
            val request = AiProviderRequestDto(
                name = "New Provider",
                provider = ModelProvider.OPENAI,
                baseUrl = "https://api.openai.com/v1",
                apiKey = "sk-new"
            )
            val responseDto = AiProviderResponseDto(
                id = "new-id",
                name = request.name,
                provider = request.provider,
                baseUrl = request.baseUrl
            )
            every { providerUsecase.createProvider(any()) } returns responseDto

            Then("200 OK와 생성된 공급자 정보를 반환한다") {
                mockMvc.perform(post("/api/v1/providers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data.id").value("new-id"))
                    .andExpect(jsonPath("$.data.name").value("New Provider"))
            }
        }
    }
})
