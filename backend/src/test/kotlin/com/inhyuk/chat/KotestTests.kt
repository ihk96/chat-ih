package com.inhyuk.chat

import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.http.client.jdk.JdkHttpClient
import dev.langchain4j.kotlin.model.chat.StreamingChatModelReply
import dev.langchain4j.kotlin.model.chat.chatFlow
import dev.langchain4j.mcp.client.DefaultMcpClient
import dev.langchain4j.mcp.client.McpClient
import dev.langchain4j.mcp.client.transport.McpTransport
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.net.http.HttpClient

@SpringBootTest
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

    test("LLM 호출 테스트") {
        val request = ChatRequest.builder()
            .messages(UserMessage.from("안녕하세요."))
            .build()

        val response = getModel().chat(request)
        println(response)
    }

    test("Stream 테스트"){
        val flow = getStreamModel().chatFlow {
            messages += UserMessage("안녕하세요.")
        }
        runBlocking {
            flow.collect { reply ->
                when (reply) {
                    is StreamingChatModelReply.PartialResponse -> {
                        print(reply.partialResponse) // Stream output as it arrives
                    }
                    is StreamingChatModelReply.CompleteResponse -> {
                        println("\nComplete: ${reply.response.aiMessage().text()}")
                    }
                    is StreamingChatModelReply.Error -> {
                        println("Error occurred: ${reply.cause.message}")
                    }
                }
            }
        }
    }
})