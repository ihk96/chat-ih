package com.inhyuk.chat.usecase

import com.inhyuk.chat.api.dto.AdminModelRequestDto
import com.inhyuk.chat.domain.model.ModelProvider
import com.inhyuk.chat.domain.model.llm.LLModel
import com.inhyuk.chat.domain.model.llm.LLModelRepository
import com.inhyuk.chat.domain.model.llm.LLModelService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class AdminLLModelUsecaseTest : BehaviorSpec({

    val repository = mockk<LLModelRepository>()
    val modelService = mockk<LLModelService>()
    val usecase = AdminLLModelUsecase(repository, modelService)

    Given("Create Model") {
        val request = AdminModelRequestDto(
            id = "gpt-4",
            publicName = "GPT 4",
            originName = "gpt-4",
            provider = ModelProvider.OPENAI,
            baseUrl = "",
            apiKey = "key",
            completionUrl = ""
        )

        When("Model ID does not exist") {
            every { repository.existsById(request.id) } returns false
            every { repository.save(any()) } returnsArgument 0

            val result = usecase.createModel(request)

            Then("It should return the created model") {
                result.id shouldBe request.id
                verify { repository.save(any()) }
            }
        }

        When("Model ID already exists") {
            every { repository.existsById(request.id) } returns true

            Then("It should throw exception") {
                shouldThrow<IllegalArgumentException> {
                    usecase.createModel(request)
                }
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
