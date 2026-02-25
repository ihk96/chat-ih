package com.inhyuk.chat

import dev.langchain4j.data.message.Content
import dev.langchain4j.model.googleai.GeminiFunctionCallingConfig
import dev.langchain4j.model.googleai.GeminiMode
import dev.langchain4j.model.googleai.GeminiThinkingConfig
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.UserMessage
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CompletableDeferred


class AiServicesTests: FunSpec({
    val model = GoogleAiGeminiStreamingChatModel.builder()
        .apiKey("")
        .returnThinking(true)
        .sendThinking(true)
        .toolConfig(GeminiFunctionCallingConfig.builder().mode(GeminiMode.AUTO).build())
        .thinkingConfig(GeminiThinkingConfig.builder().includeThoughts(true).build())
        .modelName("gemini-3-flash-preview")
        .build()

    test("Foo") {
        val assistant: TestAI = AiServices.create(TestAI::class.java, model)

        val deferred = CompletableDeferred<Unit>()

        val tokenStream = assistant.chat("안녕하세요.")
        tokenStream.onPartialThinking({
                println("Partial thinking: ${it.text()}")
            })
            .onPartialResponse({
                println("Partial response: $it")
            })
            .onCompleteResponse({
                println(it)
                deferred.complete(Unit)
            })
            .onError({
                deferred.complete(Unit)
            })
            .start()

        deferred.await()

    }

})

interface TestAI {

    fun chat(@UserMessage userMessage: String, @UserMessage contents: List<Content>): TokenStream
    fun chat(@UserMessage userMessage: String) : TokenStream


}