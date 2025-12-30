package com.inhyuk.chat.file.api.dto

import java.io.InputStream

data class FileDownloadDto(
    val fileName: String,
    val mimeType: String,
    val inputStream: InputStream
)
