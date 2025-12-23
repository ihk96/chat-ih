package com.inhyuk.chat.chat.domain.model

import com.inhyuk.chat.chat.api.dto.StreamEventDto
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.PartialResponse
import dev.langchain4j.model.chat.response.PartialResponseContext
import dev.langchain4j.model.chat.response.PartialThinking
import dev.langchain4j.model.chat.response.PartialThinkingContext
import dev.langchain4j.rag.content.Content
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.tool.BeforeToolExecution
import dev.langchain4j.service.tool.ToolExecution
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * 활성 토큰 스트림
 */
class ActiveTokenStream(
    val tokenSteam: TokenStream
) : TokenStream by tokenSteam {
    private val logger = LoggerFactory.getLogger(ActiveTokenStream::class.java)
    // 메시지 누적
    var currentType: String = ""
    var currentMessage: String = ""

    // 마지막 활동 시간
    private var lastActivityTime = LocalDateTime.now()

    /**
     * 토큰 추가 및 타입 변경 감지
     */
    fun append(type: String, chunk: String) {
        synchronized(this) {
            if (currentType.isEmpty()) {
                // 첫 토큰
                currentType = type
                currentMessage = chunk
            } else if (type == currentType) {
                // 같은 타입 - 누적만
                currentMessage += chunk
                updateActivity()
                return
            } else {
                // 타입 변경 - 이전 메시지 저장
                currentType = type
                currentMessage = chunk
            }
        }

        updateActivity()
    }

    /**
     * 스트림 정상 완료
     */
    fun complete() {
        synchronized(this) {
            // 마지막 메시지 저장
            if (currentType.isNotEmpty()) {
                currentType = ""
                currentMessage = ""
            }
        }


    }

    /**
     * 마지막 활동 시간 업데이트
     */
    private fun updateActivity() {
        lastActivityTime = LocalDateTime.now()
    }

    /**
     * 오래된 스트림인지 확인
     */
    fun isStale(minutes: Long): Boolean {
        return lastActivityTime.plusMinutes(minutes).isBefore(LocalDateTime.now())
    }


    private val partialResponseWithContextHandlers : MutableList<BiConsumer<PartialResponse?, PartialResponseContext?>> = mutableListOf()
    private val partialThinkingHandlers : MutableList<Consumer<PartialThinking?>> = mutableListOf()
    private val partialThinkingWithContextHandlers : MutableList<BiConsumer<PartialThinking?, PartialThinkingContext?>> = mutableListOf()
    private val intermediateResponseHandlers : MutableList<Consumer<ChatResponse?>> = mutableListOf()
    private val beforeToolExecutionHandlers : MutableList<Consumer<BeforeToolExecution?>> = mutableListOf()
    private val toolExecutedHandlers : MutableList<Consumer<ToolExecution?>> = mutableListOf()
    private val errorHandlers : MutableList<Consumer<Throwable?>> = mutableListOf()
    private val retrievedHandlers : MutableList<Consumer<List<Content?>?>> = mutableListOf()
    private val partialResponseHandlers : MutableList<Consumer<String?>> = mutableListOf()
    private val completeResponseHandlers : MutableList<Consumer<ChatResponse?>> = mutableListOf()

    init {
        tokenSteam.onCompleteResponse({t ->
            complete()
            completeResponseHandlers.forEach { it.accept(t) }
        }).onPartialResponse({t ->
            append("message", t)
            partialResponseHandlers.forEach { it.accept(t) }
        }).onToolExecuted({t ->
            append("function_result", t.result())
            toolExecutedHandlers.forEach { it.accept(t) }
        }).onError({t ->
            error(t)
            errorHandlers.forEach { it.accept(t) }
        }).onRetrieved({t ->
            retrievedHandlers.forEach { it.accept(t) }
        }).onPartialThinking({t ->
            append("reasoning", t.text())
            partialThinkingHandlers.forEach { it.accept(t) }
        }).onIntermediateResponse({t ->
            intermediateResponseHandlers.forEach { it.accept(t) }
        }).beforeToolExecution({t ->
            append("function_call", t.request().name())
            beforeToolExecutionHandlers.forEach { it.accept(t) }
        })

    }

    override fun start() {
        tokenSteam.start()
    }

    override fun onCompleteResponse(completeResponseHandler: Consumer<ChatResponse?>?): ActiveTokenStream {
        completeResponseHandler?.let { completeResponseHandlers.add(it) }
        return this
    }

    override fun onPartialResponse(partialResponseHandler: Consumer<String?>?): ActiveTokenStream {
        partialResponseHandler?.let { partialResponseHandlers.add(it) }
        return this
    }

    override fun onToolExecuted(toolExecuteHandler: Consumer<ToolExecution?>?): ActiveTokenStream {
        toolExecuteHandler?.let { toolExecutedHandlers.add(it) }
        return this
    }

    override fun onError(errorHandler: Consumer<Throwable?>?): ActiveTokenStream {
        errorHandler?.let { errorHandlers.add(it) }
        return this
    }

    override fun onRetrieved(contentHandler: Consumer<List<Content?>?>?): ActiveTokenStream {
        contentHandler?.let { retrievedHandlers.add(it) }
        return this
    }

//    override fun onPartialResponseWithContext(handler: BiConsumer<PartialResponse?, PartialResponseContext?>?): ActiveTokenStream {
//        handler?.let { partialResponseWithContextHandlers.add(it) }
//        return this
//    }

    override fun onPartialThinking(partialThinkingHandler: Consumer<PartialThinking?>?): ActiveTokenStream {
        partialThinkingHandler?.let { partialThinkingHandlers.add(it) }
        return this
    }

//    override fun onPartialThinkingWithContext(handler: BiConsumer<PartialThinking?, PartialThinkingContext?>?): ActiveTokenStream {
//        handler?.let { partialThinkingWithContextHandlers.add(it) }
//        return this
//    }

    override fun onIntermediateResponse(intermediateResponseHandler: Consumer<ChatResponse?>?): ActiveTokenStream {
        intermediateResponseHandler?.let { intermediateResponseHandlers.add(it) }
        return this
    }

    override fun beforeToolExecution(beforeToolExecutionHandler: Consumer<BeforeToolExecution?>?): ActiveTokenStream {
        beforeToolExecutionHandler?.let { beforeToolExecutionHandlers.add(it) }
        return this
    }

}