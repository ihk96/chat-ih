package com.inhyuk.chat.api.dto

import com.inhyuk.chat.domain.model.llm.LLModel

data class ModelResponseDto (
    val id: String,
    val modelName: String,
)

fun LLModel.toModelResponseDto() : ModelResponseDto {
    return ModelResponseDto(id, publicName)
}
