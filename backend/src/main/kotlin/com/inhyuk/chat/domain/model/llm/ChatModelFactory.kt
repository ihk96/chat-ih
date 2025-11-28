package com.inhyuk.chat.domain.model.llm

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel

interface ChatModelFactory {
    fun streamingChatModel(modelEntity : LLModel) : StreamingChatModel
    fun chatModel(modelEntity: LLModel) : ChatModel
}