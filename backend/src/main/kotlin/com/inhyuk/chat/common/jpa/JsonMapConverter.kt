package com.inhyuk.chat.common.jpa

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class JsonMapConverter : AttributeConverter<Map<String, Any>?, String?> {
    private val mapper = jacksonObjectMapper()
    private val typeRef = object : TypeReference<Map<String, Any>>() {}

    override fun convertToDatabaseColumn(attribute: Map<String, Any>?): String? {
        if (attribute.isNullOrEmpty()) {
            return null
        }
        return mapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, Any>? {
        if (dbData.isNullOrBlank()) {
            return null
        }
        return mapper.readValue(dbData, typeRef)
    }
}
