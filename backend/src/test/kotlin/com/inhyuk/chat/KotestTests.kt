package com.inhyuk.chat

import com.inhyuk.chat.domain.chat.assistant.BasicStreamAssistant
import com.inhyuk.chat.domain.chat.ChatSessionRepository
import dev.langchain4j.agent.tool.ReturnBehavior
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.agent.tool.ToolSpecifications
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.http.client.jdk.JdkHttpClient
import dev.langchain4j.kotlin.model.chat.StreamingChatModelReply
import dev.langchain4j.kotlin.model.chat.chatFlow
import dev.langchain4j.mcp.client.DefaultMcpClient
import dev.langchain4j.mcp.client.McpClient
import dev.langchain4j.mcp.client.transport.McpTransport
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.PartialThinking
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.tool.BeforeToolExecution
import dev.langchain4j.service.tool.ToolExecution
import dev.langchain4j.service.tool.ToolProvider
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CompletableDeferred
import org.springframework.boot.test.context.SpringBootTest
import java.net.http.HttpClient
import java.util.UUID

class KotestTests : FunSpec({

    fun getModel(): OpenAiChatModel {
        val httpClientBuilder = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)

        val jdkHttpClientBuilder = JdkHttpClient.builder()
            .httpClientBuilder(httpClientBuilder)

        val model = OpenAiChatModel.builder()
            .apiKey("")
            .modelName("Qwen/Qwen3-8B-AWQ")
            .httpClientBuilder(jdkHttpClientBuilder)
            .baseUrl("http://172.18.102.145:12434/v1")
            .returnThinking(true)
            .build()
        return model
    }

    fun getStreamModel(): OpenAiStreamingChatModel {
        val httpClientBuilder = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)

        val jdkHttpClientBuilder = JdkHttpClient.builder()
            .httpClientBuilder(httpClientBuilder)

        val model = OpenAiStreamingChatModel.builder()
            .apiKey("")
            .modelName("Qwen/Qwen3-8B-AWQ")
            .httpClientBuilder(jdkHttpClientBuilder)
            .baseUrl("http://172.18.102.145:12434/v1")
            .returnThinking(true)
            .build()
        return model
    }

    fun getMcpClient(): McpClient {
        val transport: McpTransport? = StreamableHttpMcpTransport.Builder()
            .url("http://192.168.50.60:8090/stream")
            .build()

        val client: McpClient = DefaultMcpClient.Builder()
            .key("McpClient")
            .transport(transport)
            .build()
        return client
    }

//    test("LLM 호출 테스트") {
//        val request = ChatRequest.builder()
//            .messages(UserMessage.from("안녕하세요."))
//            .build()
//
//        val response = getModel().chat(request)
//        println(response)
//    }
//
//    test("Stream 테스트"){
//        val flow = getStreamModel().chatFlow {
//            messages += UserMessage("안녕하세요.")
//        }
//        runBlocking {
//            flow.collect { reply ->
//                when (reply) {
//                    is StreamingChatModelReply.PartialResponse -> {
//                        print(reply.partialResponse) // Stream output as it arrives
//                    }
//                    is StreamingChatModelReply.CompleteResponse -> {
//                        println("\nComplete: ${reply.response.aiMessage().text()}")
//                    }
//                    is StreamingChatModelReply.Error -> {
//                        println("Error occurred: ${reply.cause.message}")
//                    }
//                }
//            }
//        }
//    }
//
//    test("stream할 땐 언제 memory에 저장하는지 확인 테스트"){
//        val id = UUID.randomUUID().toString()
//
//        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
//            MessageWindowChatMemory.builder()
//                .id(memoryId)
//                .maxMessages(100)
//                .chatMemoryStore(TestChatMemoryStore())
//                .build()
//        }
//
//        val assistant = AiServices.builder(TestStreamAssistant::class.java)
//            .streamingChatModel(getStreamModel())
//            .chatMemoryProvider(chatMemoryProvider)
//            .tools(TimeTools())
//            .build()
//
//        val tokenStream = assistant.chat(id, "안녕하세요, 지금은 몇시인가요?")
//        val deferred = CompletableDeferred<Unit>()
//        runBlocking {
//            tokenStream
//                .onPartialResponse { token: String ->
//                    println("Message token: $token")
//                }
//                .onPartialThinking { partialThinking: PartialThinking ->
//                    println("Thinking token: ${partialThinking.text()}")
//                }
//                .beforeToolExecution { beforeToolExecution: BeforeToolExecution ->
//                    println("Function call: ${beforeToolExecution.request().name()}")
//                }
//                .onToolExecuted { toolExecution: ToolExecution ->
//                    println("Function result: ${toolExecution.result()}")
//                }
//                .onCompleteResponse { _ ->
//                    println("Stream complete")
//                    deferred.complete(Unit)
//                }
//                .ignoreErrors()
//                .start()
//            deferred.await()
//        }
//    }
})

interface TestStreamAssistant {
    fun chat(@MemoryId memoryId: String?, @dev.langchain4j.service.UserMessage message: String?): TokenStream
}

class TestChatMemoryStore : ChatMemoryStore {
    private val cacheMemory: MutableMap<String?, MutableList<ChatMessage?>?> = mutableMapOf()

    override fun getMessages(memoryId: Any?): MutableList<ChatMessage?>? {
        println("Retrieving messages for memory ID: $memoryId")
        return cacheMemory[memoryId] ?: mutableListOf()
    }

    override fun updateMessages(memoryId: Any?, messages: MutableList<ChatMessage?>) {
        println("Updating messages for memory ID: $memoryId")
        println(messages)
        cacheMemory.put(memoryId as String?, messages)
    }

    override fun deleteMessages(memoryId: Any?) {
        println("Deleting messages for memory ID: $memoryId")
        cacheMemory.remove(memoryId)
    }
}

class TimeTools {

    @Tool(value = ["Returns current time in ISO format"], returnBehavior = ReturnBehavior.TO_LLM)
    fun getCurrentTime(): String {
        return java.time.LocalDateTime.now().toString()
    }

}