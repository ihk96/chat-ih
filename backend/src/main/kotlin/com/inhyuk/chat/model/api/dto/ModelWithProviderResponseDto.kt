package com.inhyuk.chat.model.api.dto

import com.inhyuk.chat.model.domain.LLModelEntity

data class ModelWithProviderResponseDto  (
    val id: String?,
    val modelName: String,
    val providerId: String,
    val providerName: String? = null
){

}

