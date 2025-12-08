package com.inhyuk.chat.usecase

import com.inhyuk.chat.domain.chat.ChatService
import com.inhyuk.chat.domain.chat.ChatSessionProvider
import com.inhyuk.chat.domain.chat.SummaryService
import com.inhyuk.chat.domain.chat.model.ChatSession
import com.inhyuk.chat.domain.chat.model.ChatSessionEntity
import com.inhyuk.chat.domain.model.llm.LLModelService
import dev.langchain4j.model.chat.StreamingChatModel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class BasicChatUsecaseTest : BehaviorSpec({

    val chatService = mockk<ChatService>()
    val modelService = mockk<LLModelService>()
    val sessionProvider = mockk<ChatSessionProvider>()
    val summaryService = mockk<SummaryService>(relaxed = true)

    val usecase = BasicChatUsecase(chatService, modelService, sessionProvider, summaryService)

    Given("Init Session") {
        val userId = "user1"
        val message = "Hello"
        val modelId = "gpt-4"
        val sessionId = "session1"

        When("Called") {
            val streamingModel = mockk<StreamingChatModel>()
            val session = ChatSessionEntity(id = sessionId, userId = userId)

            every { modelService.getStreamChatModel(modelId) } returns streamingModel
            every { chatService.addNewChatSession(userId) } returns session

            val result = usecase.initSession(userId, message, modelId)

            Then("It should return session ID") {
                result shouldBe sessionId
            }
            Then("Summary service should be triggered") {
                verify(exactly = 1) { summaryService.generateSummary(sessionId, message, modelId) }
            }
        }
    }

    Given("Delete Session") {
        val userId = "user1"
        val sessionId = "session1"

        When("Session exists and belongs to user") {
            val sessionEntity = ChatSessionEntity(id = sessionId, userId = userId)
            val session = ChatSession(sessionEntity)
            every { sessionProvider.getSession(sessionId) } returns session
            every { sessionProvider.deleteSession(sessionId) } just Runs

            usecase.deleteSession(userId, sessionId)

            Then("It should delete the session") {
                verify { sessionProvider.deleteSession(sessionId) }
            }
        }

        When("Session does not belong to user") {
            val sessionEntity = ChatSessionEntity(id = sessionId, userId = "otherUser")
            val session = ChatSession(sessionEntity)
            every { sessionProvider.getSession(sessionId) } returns session

            Then("It should throw IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    usecase.deleteSession(userId, sessionId)
                }
            }
        }
    }
})
