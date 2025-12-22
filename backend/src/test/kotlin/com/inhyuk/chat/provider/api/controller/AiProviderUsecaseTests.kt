package com.inhyuk.chat.provider.api.controller

import com.inhyuk.chat.provider.api.controller.dto.AiProviderRequestDto
import com.inhyuk.chat.provider.domain.AiProviderEntity
import com.inhyuk.chat.provider.domain.AiProviderRepository
import com.inhyuk.chat.provider.infrastructure.ModelDiscovery
import com.inhyuk.chat.provider.domain.ModelProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class AiProviderUsecaseTests : BehaviorSpec({
    val providerRepository = mockk<AiProviderRepository>()
    val discoveryService = mockk<ModelDiscovery>()
    val aiProviderUsecase = AiProviderUsecase(providerRepository, discoveryService)

    val providerId = "provider-1"
    val providerEntity = AiProviderEntity(
        id = providerId,
        name = "OpenAI",
        provider = ModelProvider.OPENAI,
        baseUrl = "https://api.openai.com/v1",
        apiKey = "sk-test"
    )

    Given("AiProviderUsecase가 주어졌을 때") {
        When("getProviders를 호출하면") {
            every { providerRepository.findAll() } returns listOf(providerEntity)

            val result = aiProviderUsecase.getProviders()

            Then("공급자 목록을 반환해야 한다") {
                result shouldHaveSize 1
                result[0].id shouldBe providerId
            }
        }

        When("getAvailableModels를 호출하면") {
            And("공급자가 존재하는 경우") {
                val models = listOf("gpt-3.5-turbo", "gpt-4")
                every { providerRepository.findById(providerId) } returns Optional.of(providerEntity)
                every { discoveryService.getAvailableModels(providerEntity) } returns models

                val result = aiProviderUsecase.getAvailableModels(providerId)

                Then("사용 가능한 모델 목록을 반환해야 한다") {
                    result shouldBe models
                }
            }

            And("공급자가 존재하지 않는 경우") {
                every { providerRepository.findById("invalid") } returns Optional.empty()

                Then("IllegalArgumentException이 발생해야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        aiProviderUsecase.getAvailableModels("invalid")
                    }.message shouldBe "Provider not found"
                }
            }
        }

        When("createProvider를 호출하면") {
            val request = AiProviderRequestDto(
                name = "New Provider",
                provider = ModelProvider.OPENAI,
                baseUrl = "https://api.openai.com/v1",
                apiKey = "sk-new"
            )
            val savedEntity = AiProviderEntity(
                id = "new-id",
                name = request.name,
                provider = request.provider,
                baseUrl = request.baseUrl,
                apiKey = request.apiKey
            )
            every { providerRepository.save(any()) } returns savedEntity

            val result = aiProviderUsecase.createProvider(request)

            Then("생성된 공급자 정보를 반환해야 한다") {
                result.id shouldBe "new-id"
                result.name shouldBe "New Provider"
                verify { providerRepository.save(any()) }
            }
        }

        When("getProvider를 호출하면") {
            And("공급자가 존재하는 경우") {
                every { providerRepository.findById(providerId) } returns Optional.of(providerEntity)

                val result = aiProviderUsecase.getProvider(providerId)

                Then("공급자 정보를 반환해야 한다") {
                    result.id shouldBe providerId
                    result.name shouldBe "OpenAI"
                }
            }

            And("공급자가 존재하지 않는 경우") {
                every { providerRepository.findById("invalid") } returns Optional.empty()

                Then("IllegalArgumentException이 발생해야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        aiProviderUsecase.getProvider("invalid")
                    }.message shouldBe "Provider not found"
                }
            }
        }
    }
})
