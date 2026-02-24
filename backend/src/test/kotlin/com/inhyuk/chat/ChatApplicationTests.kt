package com.inhyuk.chat

import com.fasterxml.jackson.databind.ObjectMapper
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.agent.tool.ToolSpecifications
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.kotlin.model.chat.chatFlow
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.PartialThinking
import dev.langchain4j.model.chat.response.PartialToolCall
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import dev.langchain4j.model.googleai.GeminiFunctionCallingConfig
import dev.langchain4j.model.googleai.GeminiMode
import dev.langchain4j.model.googleai.GeminiThinkingConfig
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel
import dev.langchain4j.service.tool.DefaultToolExecutor
import dev.langchain4j.service.tool.ToolExecution
import dev.langchain4j.service.tool.ToolExecutionResult
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CompletableDeferred
import org.junit.jupiter.api.Test
import java.lang.reflect.Method
import java.time.LocalDate

class ChatApplicationTests : FunSpec({

    val apiKey = ""
    val modelName = "gemini-3-flash-preview"

    val chatModel = GoogleAiGeminiChatModel.builder()
        .apiKey(apiKey)
        .modelName(modelName)
        .returnThinking(true)
        .thinkingConfig(GeminiThinkingConfig.builder().includeThoughts(true).build())
        .sendThinking(true)
        .build()

    val streamModel = GoogleAiGeminiStreamingChatModel.builder()
        .apiKey(apiKey)
        .returnThinking(true)
        .sendThinking(true)
        .toolConfig(GeminiFunctionCallingConfig.builder().mode(GeminiMode.AUTO).build())
        .thinkingConfig(GeminiThinkingConfig.builder().includeThoughts(true).build())
        .modelName(modelName)
        .build()

    fun chat(messages: List<ChatMessage>) : ChatResponse{
        val toolSpecifications = ToolSpecifications.toolSpecificationsFrom(Tools::class.java)

        val request = ChatRequest.builder()
            .messages(messages)
            .toolSpecifications(toolSpecifications)
            .build()
        val chatResponse = chatModel.chat(request)
        return chatResponse
    }

    fun stream(messages: List<ChatMessage>, handler : StreamingChatResponseHandler){
        val toolSpecifications = ToolSpecifications.toolSpecificationsFrom(Tools::class.java)

        val request = ChatRequest.builder()
            .messages(messages)
            .toolSpecifications(toolSpecifications)
            .build()
        streamModel.chat(request, handler)
    }

    test("chat test"){
        val chatResponse = chat(listOf(UserMessage.from("안녕하세요. 오늘 날씨좀 알려주세요.")))
        println(chatResponse)
    }


    test("stream test"){
        val deferred = CompletableDeferred<Unit>()
        val prompt = mutableListOf<ChatMessage>(UserMessage.from("안녕하세요. 오늘 날씨좀 알려주세요."))
        stream(prompt, CustomStreamHandler(prompt, deferred, ::stream))
        deferred.await()
    }
})

class CustomStreamHandler(val prompt : MutableList<ChatMessage>, val deferred: CompletableDeferred<Unit>, val chatMethod: (List<ChatMessage>, StreamingChatResponseHandler) -> Unit) : StreamingChatResponseHandler {
    var responsing = false
    var thinking = false
    var tools = Tools()
    override fun onPartialResponse(partialResponse: String?) {
        if(!responsing){
            responsing = true
            println("=========================================================")
            println("start response")
        }
        print(partialResponse)
    }

    override fun onPartialThinking(partialThinking: PartialThinking?) {
        if(!thinking){
            thinking = true
            println("=========================================================")
            println("start thinking")
        }
        print(partialThinking?.text())
    }

    override fun onPartialToolCall(partialToolCall: PartialToolCall?) {
        println("=========================================================")
        println("Partial tool call")
        println("toolName : ${partialToolCall?.name()}")
        println("tool Args : ${partialToolCall?.partialArguments()}")
        println("=========================================================")
    }

    override fun onCompleteResponse(p: ChatResponse?) {
        println()
        println("=========================================================")
        println("complete response")
        println(p)
        p?.let {
            val aiMessage = p.aiMessage()
            prompt.add(p.aiMessage())
            if(aiMessage.hasToolExecutionRequests()){
                println("tool calls")
                for (toolRequest in aiMessage.toolExecutionRequests()) {
                    println("toolName : ${toolRequest.name()}")
                    println("tool Args : ${toolRequest.arguments()}")
                    if(toolRequest.name() == "getDate"){
                        val toolResult = tools.getDate()
                        val toolExecutionResultMessage = ToolExecutionResultMessage.from(toolRequest,toolResult)
                        prompt.add(toolExecutionResultMessage)
                    } else if(toolRequest.name() == "getCity"){
                        val toolResult = tools.getCity()
                        val toolExecutionResultMessage = ToolExecutionResultMessage.from(toolRequest,toolResult)
                        prompt.add(toolExecutionResultMessage)
                    } else if(toolRequest.name() == "getWeather"){
                        val executor = DefaultToolExecutor(tools,toolRequest)
                        val toolResult = executor.execute(toolRequest,"0")
                        val toolExecutionResultMessage = ToolExecutionResultMessage.from(toolRequest,toolResult)
                        prompt.add(toolExecutionResultMessage)
                    }
                }
                chatMethod(prompt, this)
            } else {
                println()
                deferred.complete(Unit)
            }
        }
    }
    override fun onError(p: Throwable?) {
        println()
        println("=========================================================")
        println("error occurred")
        p?.let { println(it) }
        deferred.complete(Unit)
    }
}

class Tools {

    @Tool("Returns weather information")
    fun getWeather(
        @P("The city for which the weather forecast should be returned", required = true)
        city: String,
        @P("The date for which the weather forecast should be returned", required = true)
        date: String
    ): String {
        return "날짜 : $date, 지역 : $city, 날씨 : 맑음"
    }

    @Tool("현재 날짜를 알 수 있습니다.")
    fun getDate(): String = LocalDate.now().toString()

    @Tool("현재 사용자가 있는 City를 알 수 있습니다.")
    fun getCity(): String = "Seoul"
}

