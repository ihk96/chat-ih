package com.inhyuk.chat.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.api.dto.AdminModelRequestDto
import com.inhyuk.chat.api.dto.AdminModelResponseDto
import com.inhyuk.chat.usecase.AdminLLModelUsecase
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AdminModelControllerTest : BehaviorSpec({

    val usecase = mockk<AdminLLModelUsecase>()
    val controller = AdminModelController(usecase)
    val mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    val mapper = jacksonObjectMapper()

    Given("Get Models") {
        When("Called") {
            every { usecase.getModels() } returns emptyList()
            Then("Status OK") {
                mockMvc.perform(get("/api/admin/models"))
                    .andExpect(status().isOk)
            }
        }
    }

    Given("Create Model") {
        val request = AdminModelRequestDto(
            id = "gpt4", publicName = "GPT4", originName = "gpt-4",completionUrl = ""
        )
        val json = mapper.writeValueAsString(request)
        val responseFunc = { AdminModelResponseDto(
            id = "gpt4", publicName = "GPT4", originName = "gpt-4",baseUrl = "", completionUrl = ""
        ) }

        When("Success") {
            every { usecase.createModel(any()) } returns responseFunc()

            Then("Status OK") {
                mockMvc.perform(post("/api/admin/models")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk)
            }
        }
    }
})
