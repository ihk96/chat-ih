package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.domain.UserEntity
import com.inhyuk.chat.user.domain.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.crypto.password.PasswordEncoder

class AuthUsecaseTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val authUsecase = AuthUsecase(userRepository, passwordEncoder)

    Given("register") {
        val username = "newuser"
        val password = "password"

        When("이미 존재하는 사용자명인 경우") {
            every { userRepository.existsByUsername(username) } returns true

            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    authUsecase.register(username, password)
                }.message shouldBe "Username already exists"
            }
        }

        When("새로운 사용자명인 경우") {
            every { userRepository.existsByUsername(username) } returns false
            every { passwordEncoder.encode(password) } returns "encoded_password"
            val savedEntity = UserEntity(id = "user-1", username = username, password = "encoded_password", roles = "ROLE_USER")
            every { userRepository.save(any()) } returns savedEntity

            val result = authUsecase.register(username, password)

            Then("사용자를 저장하고 반환한다") {
                result shouldBe savedEntity
            }
        }
    }

    Given("login") {
        val username = "testuser"
        val password = "password"
        val userEntity = UserEntity(id = "user-1", username = username, password = "encoded_password", roles = "ROLE_USER")

        When("사용자가 존재하지 않는 경우") {
            every { userRepository.findByUsername(username) } returns null

            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    authUsecase.login(username, password)
                }.message shouldBe "Invalid credentials"
            }
        }

        When("비밀번호가 일치하지 않는 경우") {
            every { userRepository.findByUsername(username) } returns userEntity
            every { passwordEncoder.matches(password, userEntity.password) } returns false

            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    authUsecase.login(username, password)
                }.message shouldBe "Invalid credentials"
            }
        }

        When("인증이 성공하는 경우") {
            every { userRepository.findByUsername(username) } returns userEntity
            every { passwordEncoder.matches(password, userEntity.password) } returns true

            val result = authUsecase.login(username, password)

            Then("사용자 엔티티를 반환한다") {
                result shouldBe userEntity
            }
        }
    }
})
