package com.inhyuk.chat.common.config

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

class JwtTokenProviderTest : BehaviorSpec({

    val secret = "ZmFrZV9kZXYtc2VjcmV0LXNob3VsZC1iZS1sb25nLWVub3VnaC10by1iZS0zMmNoYXJzLWxvbmc=" // 32+ chars base64
    val expirationMillis = 10000L
    val provider = JwtTokenProvider(secret, expirationMillis)

    Given("A user details") {
        val userId = "user-123"
        val username = "testuser"
        val roles = "ROLE_USER"

        When("Generate token is called") {
            val token = provider.generateToken(userId, username, roles)

            Then("A valid token should be returned") {
                token.shouldNotBeEmpty()
            }

            Then("Token validation should succeed") {
                provider.validate(token) shouldBe true
            }

            Then("Claims should be parsable") {
                val claims = provider.parseClaims(token)
                claims.subject shouldBe userId
                claims["username"] shouldBe username
                claims["roles"] shouldBe roles
            }
        }
    }

    Given("An invalid token") {
        val invalidToken = "invalid.token.string"

        When("Validate is called") {
            val result = provider.validate(invalidToken)

            Then("It should return false") {
                result shouldBe false
            }
        }
    }
})
