package com.inhyuk.chat.file.api.dto

data class FileUploadResponseDto(
    val id: String,
    val fileName: String,
    val size: Long,
    val mimeType: String
)
