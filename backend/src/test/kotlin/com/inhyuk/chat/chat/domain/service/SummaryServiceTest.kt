package com.inhyuk.chat.chat.domain.service

import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import com.inhyuk.chat.chat.domain.repository.ChatSessionRepository
import com.inhyuk.chat.model.facade.LLModelFacade
import dev.langchain4j.model.chat.ChatModel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.*

class SummaryServiceTest : BehaviorSpec({
    val llModelFacade = mockk<LLModelFacade>()
    val sessionRepository = mockk<ChatSessionRepository>()
    val summaryService = SummaryService(llModelFacade, sessionRepository)

    Given("generateSummary") {
        val sessionId = "session-1"
        val firstMessage = "This is a long message about something."
        val modelId = "gpt-4"
        val summaryTitle = "Short Title"

        When("정상적으로 호출되면") {
            val chatModel = mockk<ChatModel>()
            val session = ChatSessionEntity(id = sessionId, userId = "user-1", title = "Original Title")

            every { llModelFacade.getChatModel(modelId) } returns chatModel
            every { chatModel.chat(any<String>()) } returns summaryTitle
            every { sessionRepository.findById(sessionId) } returns Optional.of(session)
            every { sessionRepository.save(any()) } answers { firstArg() }

            summaryService.generateSummary(sessionId, firstMessage, modelId)

            Then("모델을 통해 요약을 생성하고 세션 제목을 업데이트해야 한다") {
                verify { chatModel.chat(any<String>()) }
                verify { sessionRepository.save(match { it.title == summaryTitle }) }
            }
        }

        When("세션을 찾을 수 없는 경우") {
            every { sessionRepository.findById(sessionId) } returns Optional.empty()



            Then("아무 일도 일어나지 않아야 한다") {
                shouldThrow<IllegalArgumentException> {
                    summaryService.generateSummary(sessionId, firstMessage, modelId)
                }
            }
        }
    }
})
