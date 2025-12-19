package com.inhyuk.chat.user.application

import com.inhyuk.chat.common.config.JwtTokenProvider
import com.inhyuk.chat.user.domain.UserEntity
import com.inhyuk.chat.user.domain.UserRepository
import com.inhyuk.chat.user.domain.UserService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.Runs
import io.mockk.just
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val jwtTokenProvider = mockk<JwtTokenProvider>()

    val userService = UserService(userRepository, passwordEncoder, jwtTokenProvider)

    Given("Register request") {
        val username = "newuser"
        val password = "password"

        When("Username does not exist") {
            every { userRepository.existsByUsername(username) } returns false
            every { passwordEncoder.encode(password) } returns "encodedPass"
            every { userRepository.save(any()) } returnsArgument 0
            every { jwtTokenProvider.generateToken(any(), any(), any()) } returns "token"

            val token = userService.register(username, password)

            Then("User should be saved and token returned") {
                verify { userRepository.save(any()) }
                token shouldBe "token"
            }
        }

        When("Username already exists") {
            every { userRepository.existsByUsername(username) } returns true

            Then("It should throw IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    userService.register(username, password)
                }
            }
        }
    }

    Given("Login request") {
        val username = "user"
        val password = "password"

        When("Credentials are valid") {
            val user = UserEntity(id = "1", username = username, password = "encodedPass", roles = "USER")
            every { userRepository.findByUsername(username) } returns user
            every { passwordEncoder.matches(password, "encodedPass") } returns true
            every { jwtTokenProvider.generateToken("1", username, "USER") } returns "token"

            val token = userService.login(username, password)

            Then("Token should be returned") {
                token shouldBe "token"
            }
        }

        When("User not found") {
            every { userRepository.findByUsername(username) } returns null
            Then("It should throw exception") {
                shouldThrow<IllegalArgumentException> {
                    userService.login(username, password)
                }
            }
        }
    }
})
