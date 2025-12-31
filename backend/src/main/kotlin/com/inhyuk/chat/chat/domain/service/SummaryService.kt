package com.inhyuk.chat.chat.domain.service

import com.inhyuk.chat.chat.domain.repository.ChatSessionRepository
import com.inhyuk.chat.model.facade.LLModelFacade
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SummaryService(
    private val llModelFacade: LLModelFacade,
    private val sessionRepository: ChatSessionRepository
) {
    private val logger = LoggerFactory.getLogger(SummaryService::class.java)

    @Async
    fun generateSummary(sessionId: String, firstMessage: String, modelId: String) {
        val session = sessionRepository.findById(sessionId).orElse(null) ?: throw IllegalArgumentException("Session not found: $sessionId")
        // 요약에는 가벼운 모델이나 사용자가 선택한 모델을 사용
        // 여기서는 사용자가 선택한 모델을 그대로 사용 (또는 특정 모델 지정 가능)
        val model = llModelFacade.getChatModel(modelId)

        val prompt = """
            Summarize the following message into a short title (max 5 words).
            Do not include any other text or quotes.
            Message: $firstMessage
        """.trimIndent()

        val title = model.chat(prompt)


        session.title = title
        sessionRepository.save(session)

        logger.info("Generated summary for session $sessionId: $title")
    }
}
