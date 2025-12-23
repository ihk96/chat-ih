package com.inhyuk.chat.chat.api

import com.inhyuk.chat.chat.api.dto.ChatSessionDto
import com.inhyuk.chat.chat.domain.ChatService
import com.inhyuk.chat.chat.domain.ChatSessionProvider
import com.inhyuk.chat.chat.domain.SummaryService
import com.inhyuk.chat.model.facade.LLModelFacade
import dev.langchain4j.model.chat.StreamingChatModel
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class BasicChatUsecase(
    private val chatService: ChatService,
    private val llModelFacade: LLModelFacade,
    private val sessionProvider: ChatSessionProvider,
    private val summaryService: SummaryService
) {

    private val streamPipes = mutableMapOf<String, ChatStreamPipe>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @PreDestroy
    fun cleanup() {
        coroutineScope.cancel()
    }

    @Transactional
    fun initSession(userId: String, message: String, modelId: String): String {
        val model = llModelFacade.getStreamChatModel(modelId)
        val session = chatService.addNewChatSession(userId)

        coroutineScope.launch {
            chatSse(session.id, message, model)
        }

        // Auto-summary trigger
        summaryService.generateSummary(session.id, message, modelId)

        return session.id
    }


    @Transactional
    fun chat(userId: String, sessionId: String, message: String, modelId: String) : SseEmitter {
        val model = llModelFacade.getStreamChatModel(modelId)
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
        val pipe = ChatStreamPipe(session.id, tokenStream)
        streamPipes[sessionId] = pipe
        pipe.subscribe(emitter)
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
        streamPipes[sessionId]?.subscribe(emitter) ?:emitter.completeWithError(IllegalStateException("Session Not Active"))

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
        streamPipes.remove(session.id)?.cleanup()
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