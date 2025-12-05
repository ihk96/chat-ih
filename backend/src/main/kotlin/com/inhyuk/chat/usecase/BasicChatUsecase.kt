package com.inhyuk.chat.usecase

import com.inhyuk.chat.domain.chat.ChatService
import com.inhyuk.chat.domain.chat.ChatSessionProvider
import com.inhyuk.chat.domain.model.llm.LLModelService
import com.inhyuk.chat.usecase.dto.ChatSessionDto
import dev.langchain4j.model.chat.StreamingChatModel
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class BasicChatUsecase(
    private val chatService: ChatService,
    private val modelService: LLModelService,
    private val sessionProvider: ChatSessionProvider,
) {

    fun initSession(userId: String, message: String, modelId: String): String {
        val model = modelService.getStreamChatModel(modelId)
        val session = chatService.addNewChatSession(userId)
        chatSse(session.id, message, model)
        return session.id
    }


    fun chat(userId: String, sessionId: String, message: String, modelId: String) : SseEmitter{
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

    fun chatSse(sessionId: String, message: String, model: StreamingChatModel) : SseEmitter{
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }

        val emitter = SseEmitter()
        val tokenStream = chatService.chatStream(session, message, model)
        tokenStream.subscribe(emitter)
        tokenStream.start()

        return emitter
    }

    fun subscribe(userId: String, sessionId: String) : SseEmitter{
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

    fun getSession(userId: String, sessionId: String) : ChatSessionDto{
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }
        if(userId != session.userId){
            throw IllegalArgumentException("Session User Id Not Match")
        }
        return ChatSessionDto(session)
    }

}