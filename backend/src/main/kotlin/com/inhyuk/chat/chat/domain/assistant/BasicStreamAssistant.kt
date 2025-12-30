package com.inhyuk.chat.chat.domain.assistant

import dev.langchain4j.data.message.Content
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.V

interface BasicStreamAssistant {
    @UserMessage("{{userMessage}}")
    fun chat(@MemoryId memoryId: String?, @V("userMessage") message: String?): TokenStream

    @UserMessage("{{userMessage}}\n{{contents}}")
    fun chat(@MemoryId memoryId: String?, @V("userMessage") message: String?, @V("contents") contents : List<Content>?): TokenStream
}