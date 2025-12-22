package com.inhyuk.chat.model.api.dto


data class ModelWithProviderResponseDto  (
    val id: String?,
    val modelName: String,
    val providerId: String,
    val providerName: String? = null
){

}

