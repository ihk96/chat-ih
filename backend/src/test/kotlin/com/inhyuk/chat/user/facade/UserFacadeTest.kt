package com.inhyuk.chat.user.facade

import com.inhyuk.chat.user.domain.UserEntity
import com.inhyuk.chat.user.domain.UserRepository
import com.inhyuk.chat.user.facade.dto.UserDTO
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class UserFacadeTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>()
    val userFacade = UserFacade(userRepository)

    Given("getUserById") {
        val userId = "user-1"
        val userEntity = UserEntity(
            id = userId,
            username = "testuser",
            password = "password",
            roles = "ROLE_USER"
        )

        When("사용자가 존재하는 경우") {
            every { userRepository.findById(userId) } returns Optional.of(userEntity)

            val result = userFacade.getUserById(userId)

            Then("UserDTO를 반환한다") {
                result shouldNotBe null
                result?.id shouldBe userId
                result?.username shouldBe "testuser"
            }
        }

        When("사용자가 존재하지 않는 경우") {
            every { userRepository.findById(userId) } returns Optional.empty()

            val result = userFacade.getUserById(userId)

            Then("null을 반환한다") {
                result shouldBe null
            }
        }
    }
})
