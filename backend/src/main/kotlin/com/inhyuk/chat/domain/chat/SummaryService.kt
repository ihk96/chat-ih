package com.inhyuk.chat.domain.chat

import com.inhyuk.chat.domain.model.llm.LLModelService
import dev.langchain4j.model.chat.ChatModel
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SummaryService(
    private val modelService: LLModelService,
    private val sessionRepository: ChatSessionRepository
) {
    private val logger = LoggerFactory.getLogger(SummaryService::class.java)

    @Async
    fun generateSummary(sessionId: String, firstMessage: String, modelId: String) {
        try {
            // 요약에는 가벼운 모델이나 사용자가 선택한 모델을 사용
            // 여기서는 사용자가 선택한 모델을 그대로 사용 (또는 특정 모델 지정 가능)
            val model = modelService.getChatModel(modelId)
            
            val prompt = """
                Summarize the following message into a short title (max 5 words).
                Do not include any other text or quotes.
                Message: $firstMessage
            """.trimIndent()

            val title = model.generate(prompt)
            
            val session = sessionRepository.findById(sessionId).orElse(null) ?: return
            session.title = title
            sessionRepository.save(session)
            
            logger.info("Generated summary for session $sessionId: $title")
        } catch (e: Exception) {
            logger.error("Failed to generate summary for session $sessionId", e)
        }
    }
}
