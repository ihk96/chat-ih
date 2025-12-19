package com.inhyuk.chat.common.exception

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest : BehaviorSpec({

    val handler = GlobalExceptionHandler()

    Given("IllegalArgumentException") {
        val ex = IllegalArgumentException("Bad arg")
        When("Handled") {
            val response = handler.handleIllegalArgumentException(ex)
            Then("Status should be BAD_REQUEST") {
                response.statusCode shouldBe HttpStatus.BAD_REQUEST
            }
            Then("Body code should be BAD_REQUEST") {
                response.body?.code shouldBe "BAD_REQUEST"
                response.body?.message shouldBe "Bad arg"
            }
        }
    }

    Given("Exception (generic)") {
        val ex = RuntimeException("Boom")
        When("Handled") {
            val response = handler.handleException(ex)
            Then("Status should be INTERNAL_SERVER_ERROR") {
                response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            }
            Then("Message should be generic") {
                response.body?.message shouldBe "An unexpected error occurred"
            }
        }
    }
})
