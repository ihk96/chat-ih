package com.inhyuk.chat.domain.chat

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import java.util.function.Consumer
import kotlin.collections.getOrDefault

class CustomChatMemoryStore : ChatMemoryStore {
    private val memory: MutableMap<String?, MutableList<ChatMessage?>?> = HashMap<String?, MutableList<ChatMessage?>?>()

    override fun getMessages(memoryId: Any?): MutableList<ChatMessage?>? {
        return memory.getOrDefault(memoryId, mutableListOf<ChatMessage?>())
    }

    override fun updateMessages(memoryId: Any?, messages: MutableList<ChatMessage?>) {
        messages.forEach(Consumer { x: ChatMessage? -> println(x) })
        memory.put(memoryId as String?, messages)
    }

    override fun deleteMessages(memoryId: Any?) {
        memory.remove(memoryId)
    }
}