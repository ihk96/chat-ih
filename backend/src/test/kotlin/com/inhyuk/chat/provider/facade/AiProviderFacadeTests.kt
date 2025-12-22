package com.inhyuk.chat.provider.facade

import com.inhyuk.chat.provider.domain.AiProviderEntity
import com.inhyuk.chat.provider.domain.AiProviderRepository
import com.inhyuk.chat.provider.domain.ModelProvider
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class AiProviderFacadeTests : BehaviorSpec({
    val aiProviderRepository = mockk<AiProviderRepository>()
    val aiProviderFacade = AiProviderFacade(aiProviderRepository)

    val providerId = "provider-1"
    val providerEntity = AiProviderEntity(
        id = providerId,
        name = "OpenAI",
        provider = ModelProvider.OPENAI,
        baseUrl = "https://api.openai.com/v1",
        apiKey = "sk-test"
    )

    Given("AiProviderFacade가 주어졌을 때") {
        When("getProviders를 호출하면") {
            every { aiProviderRepository.findAll() } returns listOf(providerEntity)

            val result = aiProviderFacade.getProviders()

            Then("모든 공급자 목록을 반환해야 한다") {
                result shouldHaveSize 1
                result[0].id shouldBe providerId
                result[0].name shouldBe "OpenAI"
            }
        }

        When("getProvidersInIds를 호출하면") {
            val ids = listOf(providerId)
            every { aiProviderRepository.findAllById(ids) } returns listOf(providerEntity)

            val result = aiProviderFacade.getProvidersInIds(ids)

            Then("해당 ID들에 속하는 공급자 목록을 반환해야 한다") {
                result shouldHaveSize 1
                result[0].id shouldBe providerId
            }
        }

        When("getProviderById를 호출하면") {
            And("공급자가 존재하는 경우") {
                every { aiProviderRepository.findById(providerId) } returns Optional.of(providerEntity)

                val result = aiProviderFacade.getProviderById(providerId)

                Then("해당 공급자 DTO를 반환해야 한다") {
                    result shouldNotBe null
                    result?.id shouldBe providerId
                    result?.name shouldBe "OpenAI"
                }
            }

            And("공급자가 존재하지 않는 경우") {
                every { aiProviderRepository.findById("invalid") } returns Optional.empty()

                val result = aiProviderFacade.getProviderById("invalid")

                Then("null을 반환해야 한다") {
                    result shouldBe null
                }
            }
        }
    }
})
