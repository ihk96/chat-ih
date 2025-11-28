package com.inhyuk.chat.domain.model.llm

import com.inhyuk.chat.domain.model.ModelProvider
import dev.langchain4j.http.client.jdk.JdkHttpClient
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import java.net.http.HttpClient

object LLModelFactory {
    fun chatModel(modelEntity: LLModel): ChatModel {
        return when(modelEntity.provider){
            ModelProvider.ANTHROPIC -> AnthropicChatModelFactory.chatModel(modelEntity)
            ModelProvider.OPENAI_COMPATIBLE -> OpenAICompatibleChatModelFactory.chatModel(modelEntity)
            ModelProvider.OPENAI -> OpenAIChatModelFactory.chatModel(modelEntity)
            ModelProvider.GOOGLE -> GoogleChatModelFactory.chatModel(modelEntity)
        }
    }
    fun streamChatModel(modelEntity : LLModel) : StreamingChatModel{
        return when(modelEntity.provider){
            ModelProvider.ANTHROPIC -> AnthropicChatModelFactory.streamingChatModel(modelEntity)
            ModelProvider.OPENAI_COMPATIBLE -> OpenAICompatibleChatModelFactory.streamingChatModel(modelEntity)
            ModelProvider.OPENAI -> OpenAIChatModelFactory.streamingChatModel(modelEntity)
            ModelProvider.GOOGLE -> GoogleChatModelFactory.streamingChatModel(modelEntity)
        }
    }

    private object OpenAIChatModelFactory : ChatModelFactory{
        override fun streamingChatModel(modelEntity : LLModel) : StreamingChatModel {
            val model = OpenAiStreamingChatModel.builder()
                .apiKey(modelEntity.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
            return model
        }

        override fun chatModel(modelEntity: LLModel): ChatModel {
            val model = OpenAiChatModel.builder()
                .apiKey(modelEntity.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
            return model
        }
    }

    private object OpenAICompatibleChatModelFactory : ChatModelFactory {
        override fun streamingChatModel(modelEntity : LLModel) : StreamingChatModel{
            val httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)

            val jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(httpClientBuilder)
            val model = OpenAiStreamingChatModel.builder()
                .apiKey(modelEntity.apiKey)
                .modelName(modelEntity.originName)
                .httpClientBuilder(jdkHttpClientBuilder)
                .baseUrl(modelEntity.baseUrl) // http://172.18.102.145:12434/v1
                .returnThinking(true)
                .build()
            return model
        }

        override fun chatModel(modelEntity: LLModel): ChatModel {
            val httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)

            val jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(httpClientBuilder)

            val model = OpenAiChatModel.builder()
                .apiKey(modelEntity.apiKey)
                .modelName(modelEntity.originName)
                .httpClientBuilder(jdkHttpClientBuilder)
                .baseUrl(modelEntity.baseUrl) // http://172.18.102.145:12434/v1
                .returnThinking(true)
                .build()
            return model
        }
    }

    private object GoogleChatModelFactory : ChatModelFactory{
        override fun streamingChatModel(modelEntity: LLModel): StreamingChatModel {
            return GoogleAiGeminiStreamingChatModel.builder()
                .modelName(modelEntity.originName)
                .apiKey(modelEntity.apiKey)
                .returnThinking(true)
                .build()
        }

        override fun chatModel(modelEntity: LLModel): ChatModel {
            return GoogleAiGeminiChatModel.builder()
                .apiKey(modelEntity.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
        }
    }

    private object AnthropicChatModelFactory : ChatModelFactory{
        override fun streamingChatModel(modelEntity: LLModel): StreamingChatModel {
            return AnthropicStreamingChatModel.builder()
                .apiKey(modelEntity.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
        }

        override fun chatModel(modelEntity: LLModel): ChatModel {
            return AnthropicChatModel.builder()
                .apiKey(modelEntity.apiKey)
                .modelName(modelEntity.originName)
                .returnThinking(true)
                .build()
        }

    }

}