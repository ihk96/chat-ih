package com.inhyuk.chat.model.api

import com.inhyuk.chat.model.api.dto.AdminModelRequestDto
import com.inhyuk.chat.model.domain.LLModelRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AdminLLModelUsecaseTest : BehaviorSpec({

    val repository = mockk<LLModelRepository>()
    val usecase = AdminLLModelUsecase(repository)

    Given("Create Model") {
        val request = AdminModelRequestDto(
            publicName = "GPT 4",
            originName = "gpt-4",
            completionUrl = ""
        )

        When("Model ID does not exist") {
            every { repository.save(any()) } returnsArgument 0

            val result = usecase.createModel(request)

            Then("It should return the created model") {
                verify { repository.save(any()) }
            }
        }
    }

    Given("Delete Model") {
        val modelId = "gpt-4"

        When("Model exists") {
            every { repository.existsById(modelId) } returns true
            every { repository.deleteById(modelId) } returns Unit

            usecase.deleteModel(modelId)

            Then("It should be deleted") {
                verify { repository.deleteById(modelId) }
            }
        }

        When("Model does not exist") {
            every { repository.existsById(modelId) } returns false

            Then("It should throw exception") {
                shouldThrow<IllegalArgumentException> {
                    usecase.deleteModel(modelId)
                }
            }
        }
    }
})