package com.inhyuk.chat.user.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.user.api.dto.AuthRequestDto
import com.inhyuk.chat.user.domain.UserService
import io.kotest.core.spec.style.BehaviorSpec
import com.inhyuk.chat.common.controller.RestResponseAdvice
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class AuthControllerTest : BehaviorSpec({

    val userService = mockk<UserService>()
    val controller = AuthController(userService)
    val mapper = jacksonObjectMapper()
    val mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(RestResponseAdvice(mapper))
        .build()

    Given("Register Request") {
        val request = AuthRequestDto("user", "pass")
        val json = mapper.writeValueAsString(request)

        When("Service returns token") {
            every { userService.register("user", "pass") } returns "token"

            Then("Status should be OK and return wrapped token") {
                mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.token").value("token"))
                    .andExpect(jsonPath("$.code").value("0"))
            }
        }

        When("Service throws exception") {
            every { userService.register("user", "pass") } throws IllegalArgumentException("Error")
            
            // Note: Standalone setup might not pick up GlobalExceptionHandler unless configured.
            // For unit testing controller, we expect exception to bubble up or be handled if we attach advice.
            // Let's assume we just check it throws 500 or bubble up if no advice attached.
            // Or we should attach the GlobalExceptionHandler.
        }
    }
    
    // Attaching GlobalExceptionHandler for better test
    val mockMvcWithAdvice = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(com.inhyuk.chat.common.exception.GlobalExceptionHandler())
        .build()

    Given("Login Request with Advice") {
        val request = AuthRequestDto("user", "pass")
        val json = mapper.writeValueAsString(request)
        
        When("Invalid credentials") {
            every { userService.login("user", "pass") } throws IllegalArgumentException("Invalid")
            
            Then("Status should be BAD_REQUEST") {
                mockMvcWithAdvice.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest)
            }
        }
    }
})
