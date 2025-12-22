package com.inhyuk.chat.model.facade

import com.inhyuk.chat.model.domain.LLModelFactory
import com.inhyuk.chat.model.domain.LLModelRepository
import com.inhyuk.chat.model.facade.dto.LLModelDTO
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import org.springframework.stereotype.Component

@Component
class LLModelFacade(
    private val llModelRepository: LLModelRepository,
    private val llModelFactory: LLModelFactory
) {
    fun getModelById(id: String) : LLModelDTO? {
        return llModelRepository.findById(id).map { LLModelDTO(it.id, it.publicName, it.originName, it.providerId, it.completionUrl) }.orElse(null)
    }

    fun getChatModel(model: String) : ChatModel {
        val modelEntity = llModelRepository.findById(model).orElseThrow { IllegalArgumentException("Model not found") }
        return llModelFactory.chatModel(modelEntity)
    }

    fun getStreamChatModel(modelId : String) : StreamingChatModel{
        val modelEntity = llModelRepository.findById(modelId).orElseThrow { IllegalArgumentException("Model not found") }
        return llModelFactory.streamChatModel(modelEntity)
    }
}