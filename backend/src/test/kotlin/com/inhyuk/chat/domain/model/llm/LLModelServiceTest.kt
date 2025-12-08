package com.inhyuk.chat.domain.model.llm

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel

class LLModelServiceTest : BehaviorSpec({

    val repository = mockk<LLModelRepository>()
    val factory = mockk<LLModelFactory>()
    val service = LLModelService(repository, factory)

    Given("Get Chat Model") {
        val modelId = "gpt-4"
        
        When("Model exists") {
            val entity = mockk<LLModel>()
            val chatModel = mockk<ChatModel>()
            
            every { repository.findById(modelId) } returns Optional.of(entity)
            every { factory.chatModel(entity) } returns chatModel
            
            val result = service.getChatModel(modelId)
            
            Then("It should return the model") {
                result shouldBe chatModel
            }
        }

        When("Model not found") {
            every { repository.findById(modelId) } returns Optional.empty()
            
            Then("It should throw exception") {
                shouldThrow<IllegalArgumentException> {
                    service.getChatModel(modelId)
                }
            }
        }
    }

    Given("Get Streaming Chat Model") {
        val modelId = "gpt-4"

        When("Model exists") {
            val entity = mockk<LLModel>()
            val streamModel = mockk<StreamingChatModel>()

            every { repository.findById(modelId) } returns Optional.of(entity)
            every { factory.streamChatModel(entity) } returns streamModel

            val result = service.getStreamChatModel(modelId)

            Then("It should return the streaming model") {
                result shouldBe streamModel
            }
        }
    }
})
