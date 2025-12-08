package com.inhyuk.chat.domain.model.llm

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import org.springframework.stereotype.Service

@Service
class LLModelService(
    private val llModelRepository : LLModelRepository,
    private val llModelFactory: LLModelFactory
) {

    fun getChatModel(model: String) : ChatModel {
        val modelEntity = llModelRepository.findById(model).orElseThrow { IllegalArgumentException("Model not found") }
        val model = llModelFactory.chatModel(modelEntity)
        return model
    }

    fun getStreamChatModel(modelId : String) : StreamingChatModel{
        val modelEntity = llModelRepository.findById(modelId).orElseThrow { IllegalArgumentException("Model not found") }

        val model = llModelFactory.streamChatModel(modelEntity)
        return model
    }
}