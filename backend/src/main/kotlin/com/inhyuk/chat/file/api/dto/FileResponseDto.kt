package com.inhyuk.chat.file.api.dto

data class FileResponseDto(
    val id: String,
    val fileName: String,
    val size: Long,
    val mimeType: String,
    val userId: String,
    val isUsed: Boolean
)
