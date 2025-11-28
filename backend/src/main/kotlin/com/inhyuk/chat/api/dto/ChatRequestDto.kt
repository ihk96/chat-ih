package com.inhyuk.chat.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null

data class ChatRequestDto(
    val id : String?,
    @field:NotBlank(message = "Message cannot be blank")
    val message : String,

    @field:NotBlank(message = "Model cannot be blank")
    val model : String,
)