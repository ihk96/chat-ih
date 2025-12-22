package com.inhyuk.chat.model.domain

import com.inhyuk.chat.provider.facade.dto.AiProviderDTO
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel

interface ChatModelFactory {
    fun streamingChatModel(modelEntity: LLModelEntity, provider: AiProviderDTO): StreamingChatModel
    fun chatModel(modelEntity: LLModelEntity, provider: AiProviderDTO): ChatModel
}