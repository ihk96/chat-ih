package com.inhyuk.chat.domain.model.llm

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import org.springframework.stereotype.Service

@Service
class LLModelService(
    private val llModelRepository : LLModelRepository
) {

    fun getChatModel(model: String) : ChatModel {
        val modelEntity = llModelRepository.findById(model).orElseThrow { IllegalArgumentException("Model not found") }
        val model = LLModelFactory.chatModel(modelEntity)
        return model
    }

    fun getStreamChatModel(modelId : String) : StreamingChatModel{
        val modelEntity = llModelRepository.findById(modelId).orElseThrow { IllegalArgumentException("Model not found") }

        val model = LLModelFactory.streamChatModel(modelEntity)
        return model
    }
}