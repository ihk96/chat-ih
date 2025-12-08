package com.inhyuk.chat.domain.model.llm

import com.inhyuk.chat.domain.connection.ModelProviderConnection
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel

interface ChatModelFactory {
    fun streamingChatModel(modelEntity: LLModel, connection: ModelProviderConnection): StreamingChatModel
    fun chatModel(modelEntity: LLModel, connection: ModelProviderConnection): ChatModel
}