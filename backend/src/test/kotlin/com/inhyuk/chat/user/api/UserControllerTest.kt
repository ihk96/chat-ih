package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.api.dto.UserResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.core.Authentication
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class UserControllerTest : BehaviorSpec({
    val userUsecase = mockk<UserUsecase>()
    val controller = UserController(userUsecase)
    val mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    Given("getMe") {
        val userId = "user-123"
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns userId
        every { authentication.name } returns userId

        When("인증된 사용자인 경우") {
            val userResponse = UserResponse(userId, "testuser", "ROLE_USER")
            every { userUsecase.getMe(userId) } returns userResponse

            Then("사용자 정보를 반환한다") {
                mockMvc.perform(get("/api/v1/users/me")
                    .principal(authentication))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.id").value(userId))
                    .andExpect(jsonPath("$.data.username").value("testuser"))
                    .andExpect(jsonPath("$.data.roles").value("ROLE_USER"))
            }
        }
    }
})

