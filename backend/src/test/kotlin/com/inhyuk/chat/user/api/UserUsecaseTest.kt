package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.api.dto.UserResponse
import com.inhyuk.chat.user.domain.UserEntity
import com.inhyuk.chat.user.domain.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class UserUsecaseTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>()
    val userUsecase = UserUsecase(userRepository)

    Given("getMe") {
        val userId = "user-123"
        val userEntity = UserEntity(
            id = userId,
            username = "testuser",
            password = "password",
            roles = "ROLE_USER"
        )

        When("사용자가 존재하는 경우") {
            every { userRepository.findById(userId) } returns Optional.of(userEntity)

            val response = userUsecase.getMe(userId)

            Then("사용자 정보를 반환한다") {
                response.id shouldBe userId
                response.username shouldBe "testuser"
                response.roles shouldBe "ROLE_USER"
            }
        }

        When("사용자가 존재하지 않는 경우") {
            every { userRepository.findById(userId) } returns Optional.empty()

            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    userUsecase.getMe(userId)
                }.message shouldBe "User not found"
            }
        }
    }
})
