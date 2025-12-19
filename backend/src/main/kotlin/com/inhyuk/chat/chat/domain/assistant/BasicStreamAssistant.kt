package com.inhyuk.chat.chat.domain.assistant

import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.UserMessage

interface BasicStreamAssistant {
    fun chat(@MemoryId memoryId: String?, @UserMessage message: String?): TokenStream
}