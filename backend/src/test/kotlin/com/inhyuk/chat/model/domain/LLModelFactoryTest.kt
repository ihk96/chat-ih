package com.inhyuk.chat.model.domain

import com.inhyuk.chat.provider.domain.ModelProvider
import com.inhyuk.chat.provider.facade.AiProviderFacade
import com.inhyuk.chat.provider.facade.dto.AiProviderDTO
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk

class LLModelFactoryTest : BehaviorSpec({
    val aiProviderFacade = mockk<AiProviderFacade>()
    val llModelFactory = LLModelFactory(aiProviderFacade)

    Given("LLModelEntity와 AiProviderDTO가 주어졌을 때") {
        val modelEntity = LLModelEntity(
            id = "test-id",
            publicName = "Test Model",
            originName = "gpt-3.5-turbo",
            providerId = "provider-id",
            completionUrl = "http://localhost"
        )

        fun createProvider(provider: ModelProvider) = AiProviderDTO(
            id = "provider-id",
            name = "Test Provider",
            provider = provider,
            baseUrl = "http://localhost",
            apiKey = "test-api-key"
        )

        And("OPENAI 공급자인 경우") {
            every { aiProviderFacade.getProviderById("provider-id") } returns createProvider(ModelProvider.OPENAI)

            When("chatModel을 호출하면") {
                val model = llModelFactory.chatModel(modelEntity)
                Then("OpenAiChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<OpenAiChatModel>()
                }
            }

            When("streamChatModel을 호출하면") {
                val model = llModelFactory.streamChatModel(modelEntity)
                Then("OpenAiStreamingChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<OpenAiStreamingChatModel>()
                }
            }
        }

        And("OPENAI_COMPATIBLE 공급자인 경우") {
            every { aiProviderFacade.getProviderById("provider-id") } returns createProvider(ModelProvider.OPENAI_COMPATIBLE)

            When("chatModel을 호출하면") {
                val model = llModelFactory.chatModel(modelEntity)
                Then("OpenAiChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<OpenAiChatModel>()
                }
            }

            When("streamChatModel을 호출하면") {
                val model = llModelFactory.streamChatModel(modelEntity)
                Then("OpenAiStreamingChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<OpenAiStreamingChatModel>()
                }
            }
        }

        And("GOOGLE 공급자인 경우") {
            every { aiProviderFacade.getProviderById("provider-id") } returns createProvider(ModelProvider.GOOGLE)

            When("chatModel을 호출하면") {
                val model = llModelFactory.chatModel(modelEntity)
                Then("GoogleAiGeminiChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<GoogleAiGeminiChatModel>()
                }
            }

            When("streamChatModel을 호출하면") {
                val model = llModelFactory.streamChatModel(modelEntity)
                Then("GoogleAiGeminiStreamingChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<GoogleAiGeminiStreamingChatModel>()
                }
            }
        }

        And("ANTHROPIC 공급자인 경우") {
            every { aiProviderFacade.getProviderById("provider-id") } returns createProvider(ModelProvider.ANTHROPIC)

            When("chatModel을 호출하면") {
                val model = llModelFactory.chatModel(modelEntity)
                Then("AnthropicChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<AnthropicChatModel>()
                }
            }

            When("streamChatModel을 호출하면") {
                val model = llModelFactory.streamChatModel(modelEntity)
                Then("AnthropicStreamingChatModel 인스턴스를 반환해야 한다") {
                    model.shouldBeInstanceOf<AnthropicStreamingChatModel>()
                }
            }
        }

        And("공급자를 찾을 수 없는 경우") {
            every { aiProviderFacade.getProviderById("provider-id") } returns null

            When("chatModel을 호출하면") {
                Then("IllegalArgumentException이 발생해야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        llModelFactory.chatModel(modelEntity)
                    }.message shouldBe "Provider not found"
                }
            }
        }
    }
})
