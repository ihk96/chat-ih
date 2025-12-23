package com.inhyuk.chat.user.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.user.api.dto.AuthRequestDto
import com.inhyuk.chat.user.domain.UserEntity
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest : BehaviorSpec({
    val authUsecase = mockk<AuthUsecase>()
    val controller = AuthController(authUsecase)
    val mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    val mapper = jacksonObjectMapper()

    Given("register") {
        val requestDto = AuthRequestDto(username = "newuser", password = "password")
        val json = mapper.writeValueAsString(requestDto)

        When("회원가입 성공") {
            val userEntity = UserEntity(id = "user-1", username = "newuser", password = "encoded_password", roles = "ROLE_USER")
            every { authUsecase.register("newuser", "password") } returns userEntity

            Then("200 OK를 반환한다") {
                mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk)
            }
        }
    }

    Given("login") {
        val requestDto = AuthRequestDto(username = "testuser", password = "password")
        val json = mapper.writeValueAsString(requestDto)

        When("로그인 성공") {
            val userEntity = UserEntity(id = "user-1", username = "testuser", password = "encoded_password", roles = "ROLE_USER")
            every { authUsecase.login("testuser", "password") } returns userEntity

            Then("200 OK를 반환한다") {
                mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk)
            }
        }
    }
})
