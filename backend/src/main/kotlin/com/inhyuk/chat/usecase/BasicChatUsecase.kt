package com.inhyuk.chat.usecase

import com.inhyuk.chat.domain.chat.ChatService
import com.inhyuk.chat.domain.chat.ChatSessionTokenStream
import com.inhyuk.chat.domain.model.llm.LLModelService
import com.inhyuk.chat.usecase.dto.StreamEventDto
import dev.langchain4j.model.chat.response.PartialThinking
import dev.langchain4j.service.tool.BeforeToolExecution
import dev.langchain4j.service.tool.ToolExecution
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@Component
class BasicChatUsecase(
    private val chatService: ChatService,
    private val modelService: LLModelService
) {

    val sessionTokenMap: MutableMap<String, ChatSessionTokenStream> = mutableMapOf()

    fun initSession(userId: String, message: String, modelId: String): String {
        val session = chatService.addNewChatSession(userId)
        sse(session.id, message, modelId)
        return session.id
    }

    fun sse(sessionId: String, message: String, modelId: String) : SseEmitter{
        val model = modelService.getStreamChatModel(modelId)
        if(sessionTokenMap.containsKey(sessionId)){
            throw IllegalArgumentException("Session already exists")
        }
        val sessionTokenStream = ChatSessionTokenStream(sessionId = sessionId)
        sessionTokenMap[sessionId] = sessionTokenStream

        val emitter = SseEmitter()
        sessionTokenStream.subscribe(emitter)

        val tokenStream = chatService.chatStream(sessionId, message, model)

        tokenStream
            .onPartialResponse { token: String ->
                sessionTokenStream.append("message", token)
            }
            .onPartialThinking { partialThinking: PartialThinking ->
                sessionTokenStream.append("reasoning", partialThinking.text())
            }
            .beforeToolExecution { beforeToolExecution: BeforeToolExecution ->
                sessionTokenStream.append("function_call", beforeToolExecution.request().name())
            }
            .onToolExecuted { toolExecution: ToolExecution ->
                sessionTokenStream.append("function_result", toolExecution.result())
            }
            .onCompleteResponse { _ ->
                sessionTokenStream.complete()
                sessionTokenMap.remove(sessionId)
            }
            .ignoreErrors()
            .start()

        return emitter
    }

}