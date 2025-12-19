package com.inhyuk.chat.model.api.dto

import com.inhyuk.chat.model.domain.LLModelEntity

data class AdminModelRequestDto(
    val publicName: String,
    val originName: String,
    val providerId: String = "",
    val completionUrl: String = "",
)

data class AdminModelResponseDto(
    val id: String,
    val publicName: String,
    val originName: String,
    val providerId: String,
    val completionUrl: String
) {
    constructor(entity: LLModelEntity) : this (
        id = entity.id,
        publicName = entity.publicName,
        originName = entity.originName,
        providerId = entity.providerId,
        completionUrl = entity.completionUrl
    )
}
