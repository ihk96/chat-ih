package com.inhyuk.chat.model.domain

import com.inhyuk.chat.model.domain.LLModelRepository
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
        return llModelFactory.chatModel(modelEntity)
    }

    fun getStreamChatModel(modelId : String) : StreamingChatModel{
        val modelEntity = llModelRepository.findById(modelId).orElseThrow { IllegalArgumentException("Model not found") }
        return llModelFactory.streamChatModel(modelEntity)
    }
}