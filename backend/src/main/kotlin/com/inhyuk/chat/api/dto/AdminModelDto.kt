package com.inhyuk.chat.api.dto

import com.inhyuk.chat.domain.model.ModelProvider
import com.inhyuk.chat.domain.model.llm.LLModel

data class AdminModelRequestDto(
    val id: String,
    val publicName: String,
    val originName: String,
    val provider: ModelProvider,
    val baseUrl: String = "",
    val apiKey: String = "",
    val completionUrl: String = ""
) {
    fun toEntity(): LLModel {
        return LLModel(
            id = id,
            publicName = publicName,
            originName = originName,
            provider = provider,
            baseUrl = baseUrl,
            apiKey = apiKey,
            completionUrl = completionUrl
        )
    }
}

data class AdminModelResponseDto(
    val id: String,
    val publicName: String,
    val originName: String,
    val provider: ModelProvider,
    val baseUrl: String,
    // apiKey는 보안상 응답에서 제외하거나 마스킹 처리하는 것이 좋음. 여기서는 제외.
    val completionUrl: String
) {
    constructor(entity: LLModel) : this(
        id = entity.id,
        publicName = entity.publicName,
        originName = entity.originName,
        provider = entity.provider,
        baseUrl = entity.baseUrl,
        completionUrl = entity.completionUrl
    )
}
