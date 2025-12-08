package com.inhyuk.chat.common

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.test.util.ReflectionTestUtils

class StringCryptoConverterTest : BehaviorSpec({

    val converter = StringCryptoConverter()
    // Inject a 32-byte key for testing
    ReflectionTestUtils.setField(converter, "key", "12345678901234567890123456789012")

    Given("A plain text string") {
        val original = "secret_api_key"

        When("It is encrypted") {
            val encrypted = converter.convertToDatabaseColumn(original)

            Then("It should not be null") {
                encrypted shouldNotBe null
            }

            Then("It should not match the original") {
                encrypted shouldNotBe original
            }

            Then("It should be decryptable back to original") {
                val decrypted = converter.convertToEntityAttribute(encrypted)
                decrypted shouldBe original
            }
        }
    }

    Given("Null input") {
        When("Encrypting null") {
            val result = converter.convertToDatabaseColumn(null)
            Then("Result should be null") { result shouldBe null }
        }
        When("Decrypting null") {
            val result = converter.convertToEntityAttribute(null)
            Then("Result should be null") { result shouldBe null }
        }
    }
})
