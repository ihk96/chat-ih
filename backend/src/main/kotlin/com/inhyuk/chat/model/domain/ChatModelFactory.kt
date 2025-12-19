package com.inhyuk.chat.model.domain

import com.inhyuk.chat.provider.domain.AiProvider
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel

interface ChatModelFactory {
    fun streamingChatModel(modelEntity: LLModelEntity, provider: AiProvider): StreamingChatModel
    fun chatModel(modelEntity: LLModelEntity, provider: AiProvider): ChatModel
}