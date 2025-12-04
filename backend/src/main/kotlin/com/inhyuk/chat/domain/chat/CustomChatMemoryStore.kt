package com.inhyuk.chat.domain.chat

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.store.memory.chat.ChatMemoryStore

class CustomChatMemoryStore (
    val repository : ChatSessionRepository
) : ChatMemoryStore {
    private val cacheMemory: MutableMap<String?, MutableList<ChatMessage?>?> = mutableMapOf()

    override fun getMessages(memoryId: Any?): MutableList<ChatMessage?>? {
        return cacheMemory.getOrPut(memoryId as String?, {
            memoryId?.let {
                repository.findById(it).orElseThrow {
                    IllegalArgumentException("Chat session not found for ID: $it")
                }.messages
            } as MutableList<ChatMessage?>?
        })
    }

    override fun updateMessages(memoryId: Any?, messages: MutableList<ChatMessage?>) {
        cacheMemory.put(memoryId as String?, messages)
        memoryId?.let { repository.findById(it).ifPresent { sessionEntity ->
            sessionEntity.messages = messages as MutableList<ChatMessage>
            repository.save(sessionEntity)
        }}
    }

    override fun deleteMessages(memoryId: Any?) {
        cacheMemory.remove(memoryId)
    }
}