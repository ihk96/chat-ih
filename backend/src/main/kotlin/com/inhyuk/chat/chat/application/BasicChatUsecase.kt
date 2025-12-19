package com.inhyuk.chat.chat.application

import com.inhyuk.chat.chat.api.dto.ChatSessionDto
import com.inhyuk.chat.chat.domain.ChatService
import com.inhyuk.chat.chat.domain.ChatSessionProvider
import com.inhyuk.chat.chat.domain.SummaryService
import com.inhyuk.chat.model.domain.LLModelService
import dev.langchain4j.model.chat.StreamingChatModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class BasicChatUsecase(
    private val chatService: ChatService,
    private val modelService: LLModelService,
    private val sessionProvider: ChatSessionProvider,
    private val summaryService: SummaryService
) {

    @Transactional
    fun initSession(userId: String, message: String, modelId: String): String {
        val model = modelService.getStreamChatModel(modelId)
        val session = chatService.addNewChatSession(userId)
        chatSse(session.id, message, model)

        // Auto-summary trigger
        summaryService.generateSummary(session.id, message, modelId)

        return session.id
    }


    @Transactional
    fun chat(userId: String, sessionId: String, message: String, modelId: String) : SseEmitter {
        val model = modelService.getStreamChatModel(modelId)
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }
        val entity = session.entity
        if(entity.userId != userId) {
            throw IllegalArgumentException("Session User Id Not Match")
        }
        if(session.isActive){
            throw IllegalArgumentException("Session Already Active")
        }

        return chatSse(session.id, message, model)
    }

    fun chatSse(sessionId: String, message: String, model: StreamingChatModel) : SseEmitter {
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }

        val emitter = SseEmitter()
        val tokenStream = chatService.chatStream(session, message, model)
        tokenStream.subscribe(emitter)
        tokenStream.start()

        return emitter
    }

    fun subscribe(userId: String, sessionId: String) : SseEmitter {
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }
        if(userId != session.userId){
            throw IllegalArgumentException("Session User Id Not Match")
        }
        if(!session.isActive){
            throw IllegalArgumentException("Session Not Active")
        }

        val emitter = SseEmitter()
        session.activeTokenStream?.subscribe(emitter)

        return emitter
    }

    fun getSession(userId: String, sessionId: String) : ChatSessionDto {
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }
        if(userId != session.userId){
            throw IllegalArgumentException("Session User Id Not Match")
        }
        return ChatSessionDto(session)
    }

    fun getSessions(userId: String, pageable: Pageable): Page<ChatSessionDto> {
        return sessionProvider.getSessions(userId, pageable).map { ChatSessionDto(it) }
    }

    @Transactional
    fun deleteSession(userId: String, sessionId: String) {
        val session = sessionProvider.getSession(sessionId) ?: throw IllegalArgumentException("Session Not Found")
        if (session.userId != userId) {
            throw IllegalArgumentException("Session User Id Not Match")
        }
        sessionProvider.deleteSession(sessionId)
    }

    @Transactional
    fun updateSessionTitle(userId: String, sessionId: String, title: String) {
        val session = sessionProvider.getSession(sessionId) ?: throw IllegalArgumentException("Session Not Found")
        if (session.userId != userId) {
           throw IllegalArgumentException("Session User Id Not Match")
        }
        session.entity.title = title
        sessionProvider.saveSession(session.entity)
    }
}