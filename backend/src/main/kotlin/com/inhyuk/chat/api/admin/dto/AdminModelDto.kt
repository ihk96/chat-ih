package com.inhyuk.chat.api.admin.dto

import com.inhyuk.chat.domain.model.llm.LLModel

data class AdminModelRequestDto(
    val id: String,
    val publicName: String,
    val originName: String,
    val connectionId: String = "",
    val completionUrl: String = "",
) {
    fun toEntity(): LLModel {
        return LLModel(
            id = id,
            publicName = publicName,
            originName = originName,
            connectionId = connectionId,
            completionUrl = completionUrl
        )
    }
}

data class AdminModelResponseDto(
    val id: String,
    val publicName: String,
    val originName: String,
    val baseUrl: String,
    // apiKey는 보안상 응답에서 제외하거나 마스킹 처리하는 것이 좋음. 여기서는 제외.
    val completionUrl: String
) {
    constructor(entity: LLModel) : this(
        id = entity.id,
        publicName = entity.publicName,
        originName = entity.originName,
        baseUrl = "",
        completionUrl = entity.completionUrl
    )
}
