package com.inhyuk.chat.model.facade.dto

data class LLModelDTO(
    val id: String? =null,
    val publicName: String,
    val originName: String,
    val providerId: String,
    val completionUrl: String? = null
)