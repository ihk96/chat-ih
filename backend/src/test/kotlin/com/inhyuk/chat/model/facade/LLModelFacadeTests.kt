package com.inhyuk.chat.model.facade

import com.inhyuk.chat.model.domain.LLModelEntity
import com.inhyuk.chat.model.domain.LLModelFactory
import com.inhyuk.chat.model.domain.LLModelRepository
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class LLModelFacadeTests : BehaviorSpec({
    val llModelRepository = mockk<LLModelRepository>()
    val llModelFactory = mockk<LLModelFactory>()
    val llModelFacade = LLModelFacade(llModelRepository, llModelFactory)

    val modelId = "model-1"
    val modelEntity = LLModelEntity(
        id = modelId,
        publicName = "GPT-4",
        originName = "gpt-4",
        providerId = "provider-1",
        completionUrl = "http://example.com"
    )

    Given("LLModelFacade가 주어졌을 때") {

        When("getModelById를 호출하면") {
            And("모델이 존재하는 경우") {
                every { llModelRepository.findById(modelId) } returns Optional.of(modelEntity)

                val result = llModelFacade.getModelById(modelId)

                Then("해당 모델 DTO를 반환해야 한다") {
                    result shouldNotBe null
                    result?.id shouldBe modelId
                    result?.publicName shouldBe "GPT-4"
                }
            }

            And("모델이 존재하지 않는 경우") {
                every { llModelRepository.findById("non-existent") } returns Optional.empty()

                val result = llModelFacade.getModelById("non-existent")

                Then("null을 반환해야 한다") {
                    result shouldBe null
                }
            }
        }

        When("getChatModel을 호출하면") {
            And("모델이 존재하는 경우") {
                val chatModel = mockk<ChatModel>()
                every { llModelRepository.findById(modelId) } returns Optional.of(modelEntity)
                every { llModelFactory.chatModel(modelEntity) } returns chatModel

                val result = llModelFacade.getChatModel(modelId)

                Then("ChatModel 인스턴스를 반환해야 한다") {
                    result shouldBe chatModel
                }
            }

            And("모델이 존재하지 않는 경우") {
                every { llModelRepository.findById(any()) } returns Optional.empty()

                Then("IllegalArgumentException이 발생해야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        llModelFacade.getChatModel("invalid")
                    }.message shouldBe "Model not found"
                }
            }
        }

        When("getStreamChatModel을 호출하면") {
            And("모델이 존재하는 경우") {
                val streamingChatModel = mockk<StreamingChatModel>()
                every { llModelRepository.findById(modelId) } returns Optional.of(modelEntity)
                every { llModelFactory.streamChatModel(modelEntity) } returns streamingChatModel

                val result = llModelFacade.getStreamChatModel(modelId)

                Then("StreamingChatModel 인스턴스를 반환해야 한다") {
                    result shouldBe streamingChatModel
                }
            }

            And("모델이 존재하지 않는 경우") {
                every { llModelRepository.findById(any()) } returns Optional.empty()

                Then("IllegalArgumentException이 발생해야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        llModelFacade.getStreamChatModel("invalid")
                    }.message shouldBe "Model not found"
                }
            }
        }
    }
})