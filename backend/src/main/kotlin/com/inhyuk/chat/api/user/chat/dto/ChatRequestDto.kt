package com.inhyuk.chat.api.user.chat.dto

import jakarta.validation.constraints.NotBlank

data class ChatRequestDto(
    val id : String?,
    @field:NotBlank(message = "Message cannot be blank")
    val message : String,

    @field:NotBlank(message = "Model cannot be blank")
    val model : String,
)