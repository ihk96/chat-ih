package com.inhyuk.chat.file.domain

import java.io.InputStream

interface FileStorageProvider {
    fun store(inputStream: InputStream, storagePath: String): String
    fun load(storagePath: String): InputStream
    fun delete(storagePath: String)
}
