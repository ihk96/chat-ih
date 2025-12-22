package com.inhyuk.chat.model.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.model.api.dto.AdminModelRequestDto
import com.inhyuk.chat.model.api.dto.AdminModelResponseDto
import com.inhyuk.chat.common.controller.RestResponseAdvice
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class AdminModelControllerTest : BehaviorSpec({

    val usecase = mockk<AdminLLModelUsecase>()
    val controller = AdminModelController(usecase)
    val mapper = jacksonObjectMapper()
    val mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .build()

    Given("Get Models") {
        When("Called") {
            every { usecase.getModels() } returns emptyList()
            Then("Status OK and wrapped") {
                val result = mockMvc.perform(get("/api/v1/admin/models"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data").isArray)
            }
        }
    }

    Given("Create Model") {
        val request = AdminModelRequestDto(
            publicName = "GPT4", originName = "gpt-4", providerId = "openai", completionUrl = ""
        )
        val json = mapper.writeValueAsString(request)
        val responseFunc = { AdminModelResponseDto(
            id = "gpt4", publicName = "GPT4", originName = "gpt-4", providerId = "openai", completionUrl = ""
        ) }

        When("Success") {
            every { usecase.createModel(any()) } returns responseFunc()

            Then("Status OK and wrapped") {
                mockMvc.perform(post("/api/v1/admin/models")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data.id").value("gpt4"))
            }
        }
    }
})
