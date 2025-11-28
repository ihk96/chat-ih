package com.inhyuk.chat.usecase

import com.inhyuk.chat.domain.chat.ChatService
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
    fun sse(id: String?, message: String, modelId: String) : SseEmitter{
        val emitter = SseEmitter()

        val model = modelService.getStreamChatModel(modelId)
        val tokenStream = chatService.chatStream(id, message, model)

        tokenStream
            .onPartialResponse { token: String ->
                sendEvent(emitter, "message", token)
            }
            .onPartialThinking { partialThinking: PartialThinking ->
                sendEvent(emitter, "reasoning", partialThinking.text())
            }
            .beforeToolExecution { beforeToolExecution: BeforeToolExecution ->
                sendEvent(emitter, "function_call", beforeToolExecution.request().name())
            }
            .onToolExecuted { toolExecution: ToolExecution ->
                sendEvent(emitter, "function_result", toolExecution.result())
            }
            .onCompleteResponse { _ -> emitter.complete() }
            .ignoreErrors()
            .start()

        sendEvent(emitter, "chat_id", id)

        return emitter
    }

    private fun sendEvent(emitter: SseEmitter, type: String?, content: String?) {
        sendEvent(emitter, type, content, null)
    }

    private fun sendEvent(emitter: SseEmitter, type: String?, content: String?, metadata: MutableMap<String?, Any?>?) {
        try {
            val event = StreamEventDto(type, content, metadata)
            emitter.send(SseEmitter.event().data(event))
        } catch (e: IOException) {
            emitter.completeWithError(e)
        }
    }
}