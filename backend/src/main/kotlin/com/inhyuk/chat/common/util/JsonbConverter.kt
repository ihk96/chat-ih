package com.inhyuk.chat.common.util

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ChatMessageDeserializer
import dev.langchain4j.data.message.ChatMessageSerializer
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class JsonbConverter : AttributeConverter<List<ChatMessage>, String> {

    override fun convertToDatabaseColumn(messages: List<ChatMessage>?): String? {
        return ChatMessageSerializer.messagesToJson(messages)
    }

    override fun convertToEntityAttribute(json: String?): List<ChatMessage>? {
        return ChatMessageDeserializer.messagesFromJson(json) // LangChain4j 헬퍼
    }
}