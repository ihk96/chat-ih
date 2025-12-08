package com.inhyuk.chat.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.api.dto.ChatRequestDto
import com.inhyuk.chat.api.dto.UpdateSessionRequestDto
import com.inhyuk.chat.usecase.BasicChatUsecase
import com.inhyuk.chat.usecase.dto.ChatSessionDto
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.Principal

class ChatControllerTest : BehaviorSpec({

    val chatUsecase = mockk<BasicChatUsecase>()
    val controller = ChatController(chatUsecase)
    // We need to inject a mock Authentication principal logic.
    // Since we are using standalone setup, we can't easily rely on Spring Security context holder 
    // populated by filters unless we mock it or pass it.
    // Controller methods take `Authentication` as argument.
    // Standalone MockMvc resolves Principal/Authentication if we pass it in request.

    val mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    val mapper = jacksonObjectMapper()

    Given("Create Chat Session") {
        val request = ChatRequestDto(id = null, message = "Hi", model = "gpt4")
        val json = mapper.writeValueAsString(request)
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns "user1"
        every { authentication.name } returns "user1"

        When("Success") {
            every { chatUsecase.initSession("user1", "Hi", "gpt4") } returns "sess1"

            Then("Return Session ID") {
                mockMvc.perform(post("/api/chat/v1/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .principal(authentication)) 
                    .andExpect(status().isOk)
            }
        }
    }
})
