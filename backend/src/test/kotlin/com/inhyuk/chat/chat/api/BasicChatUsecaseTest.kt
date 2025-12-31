package com.inhyuk.chat.chat.api

import com.inhyuk.chat.chat.domain.service.ChatService
import com.inhyuk.chat.chat.domain.ChatSessionProvider
import com.inhyuk.chat.chat.domain.repository.ChatSessionRepository
import com.inhyuk.chat.chat.domain.service.ChatAttachmentService
import com.inhyuk.chat.chat.domain.service.SummaryService
import com.inhyuk.chat.chat.domain.model.ActiveTokenStream
import com.inhyuk.chat.chat.domain.model.ChatMemoryEntity
import com.inhyuk.chat.chat.domain.model.ChatSession
import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import com.inhyuk.chat.chat.domain.repository.ChatAttachmentRepository
import com.inhyuk.chat.chat.domain.repository.ChatMessageRepository
import com.inhyuk.chat.file.facade.FileFacade
import com.inhyuk.chat.model.facade.LLModelFacade
import dev.langchain4j.model.chat.StreamingChatModel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

class BasicChatUsecaseTest : BehaviorSpec({

    val chatService = mockk<ChatService>()
    val llModelFacade = mockk<LLModelFacade>()
    val sessionProvider = mockk<ChatSessionProvider>()
    val summaryService = mockk<SummaryService>(relaxed = true)
    val fileFacade = mockk<FileFacade>()
    val chatAttachmentService = mockk<ChatAttachmentService>()
    val chatSessionRepository = mockk<ChatSessionRepository>()
    val chatMessageRepository = mockk<ChatMessageRepository>()
    val chatAttachmentRepository = mockk<ChatAttachmentRepository>()

    val usecase = BasicChatUsecase(
        chatService,
        llModelFacade,
        sessionProvider,
        summaryService,
        fileFacade,
        chatAttachmentService,
        chatSessionRepository,
        chatMessageRepository,
        chatAttachmentRepository
    )

    Given("Init Session") {
        val userId = "user1"
        val message = "Hello"
        val modelId = "gpt-4"
        val sessionId = "session1"

        When("Called") {
            val streamingModel = mockk<StreamingChatModel>()
            val session = ChatSessionEntity(id = sessionId, userId = userId)
            val memory = ChatMemoryEntity(chatSessionId = sessionId)

            every { llModelFacade.getStreamChatModel(modelId) } returns streamingModel
            every { chatService.addNewChatSession(userId) } returns session
            every { chatService.chatStream(any(), any(), streamingModel, any())} returns mockk<ActiveTokenStream>(){
                every { currentType } returns ""
                every { currentMessage } returns ""
                every { start() } returns Unit

            }
            every { sessionProvider.getSession(sessionId) } returns ChatSession(session, memory)

            val result = usecase.initSession(userId, message, modelId, null)

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
            val memory = ChatMemoryEntity(chatSessionId = sessionId)
            val session = ChatSession(sessionEntity, memory)
            every { sessionProvider.getSession(sessionId) } returns session
            every { sessionProvider.deleteSession(sessionId) } just Runs

            usecase.deleteSession(userId, sessionId)

            Then("It should delete the session") {
                verify { sessionProvider.deleteSession(sessionId) }
            }
        }

        When("Session does not belong to user") {
            val sessionEntity = ChatSessionEntity(id = sessionId, userId = "otherUser")
            val memory = ChatMemoryEntity(chatSessionId = sessionId)
            val session = ChatSession(sessionEntity, memory)
            every { sessionProvider.getSession(sessionId) } returns session

            Then("It should throw IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    usecase.deleteSession(userId, sessionId)
                }
            }
        }
    }
})