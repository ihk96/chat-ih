package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.domain.model.ChatMemoryEntity
import com.inhyuk.chat.chat.domain.model.ChatSessionEntity
import com.inhyuk.chat.chat.domain.repository.ChatMemoryRepository
import com.inhyuk.chat.chat.domain.repository.ChatMessageRepository
import com.inhyuk.chat.chat.domain.repository.ChatSessionRepository
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.util.*

class ChatSessionProviderTest : BehaviorSpec({
    val sessionRepository = mockk<ChatSessionRepository>()
    val messageRepository = mockk<ChatMessageRepository>()
    val memoryRepository = mockk<ChatMemoryRepository>()
    
    // ChatSessionProvider는 init 블록에서 스케줄러를 시작하므로
    // 테스트 종료 시 정리가 필요할 수 있음 (여기서는 일단 생성)
    val provider = ChatSessionProvider(sessionRepository, messageRepository, memoryRepository)

    Given("getSession") {
        val sessionId = "session-1"
        val userId = "user-1"
        val entity = ChatSessionEntity(id = sessionId, userId = userId)
        val memory = ChatMemoryEntity(chatSessionId = sessionId)

        When("캐시에 없고 DB에 존재할 때") {
            every { sessionRepository.findById(sessionId) } returns Optional.of(entity)
            every { memoryRepository.findByChatSessionId(sessionId) } returns memory
            every { messageRepository.findByChatSessionId(sessionId) } returns emptyList()

            val result = provider.getSession(sessionId)

            Then("DB에서 조회하여 캐싱하고 반환해야 한다") {
                result shouldNotBe null
                result?.id shouldBe sessionId
                verify { sessionRepository.findById(sessionId) }
            }
        }

        When("캐시에 존재할 때") {
            // 위에서 이미 캐싱됨
            val result = provider.getSession(sessionId)

            Then("캐시된 정보를 반환해야 한다") {
                result shouldNotBe null
                // DB 조회는 최초 1회만 발생해야 함 (위의 When 절 포함 총 1회)
                verify(exactly = 1) { sessionRepository.findById(sessionId) }
            }
        }
    }

    Given("ChatMemoryStore 구현") {
        val sessionId = "session-1"
        val messages = mutableListOf<ChatMessage?>(
            UserMessage.from("Hi"),
            AiMessage.from("Hello")
        )

        When("updateMessages 호출 시") {
            val memory = ChatMemoryEntity(chatSessionId = sessionId, messages = "")
            every { memoryRepository.findByChatSessionId(sessionId) } returns memory
            every { memoryRepository.save(any()) } answers { firstArg() }
            every { messageRepository.saveAll(any<List<com.inhyuk.chat.chat.domain.model.ChatMessageEntity>>()) } returns emptyList()

            provider.updateMessages(sessionId, messages)

            Then("메모리 엔티티의 메시지가 업데이트되고 저장되어야 한다") {
                verify { memoryRepository.save(match { it.messages.isNotEmpty() }) }
            }
        }

        When("getMessages 호출 시") {
            val result = provider.getMessages(sessionId)

            Then("저장된 메시지들을 반환해야 한다") {
                result?.size shouldBe 2
                result?.get(0) shouldBe messages[0]
            }
        }
    }
})
