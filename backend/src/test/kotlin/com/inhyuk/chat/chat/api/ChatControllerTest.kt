package com.inhyuk.chat.chat.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.chat.api.dto.ChatRequestDto
import com.inhyuk.chat.chat.api.BasicChatUsecase
import com.inhyuk.chat.common.controller.RestResponseAdvice
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class ChatControllerTest : BehaviorSpec({

    val chatUsecase = mockk<BasicChatUsecase>()
    val controller = ChatController(chatUsecase)
    val mapper = jacksonObjectMapper()
    val mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .build()

    Given("Create Chat Session") {
        val request = ChatRequestDto(id = null, message = "Hi", model = "gpt4")
        val json = mapper.writeValueAsString(request)
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns "user1"
        every { authentication.name } returns "user1"

        When("Success") {
            every { chatUsecase.initSession("user1", "Hi", "gpt4") } returns "sess1"

            Then("Return wrapped Session ID") {
                mockMvc.perform(post("/api/v1/chat/v1/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .principal(authentication)) 
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data").value("sess1"))
            }
        }
    }
})
