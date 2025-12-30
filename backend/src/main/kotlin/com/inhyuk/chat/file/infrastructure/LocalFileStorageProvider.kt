package com.inhyuk.chat.file.infrastructure

import com.inhyuk.chat.file.domain.FileStorageProvider
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Component
class LocalFileStorageProvider(
    @Value("\${app.file.upload-dir}")
    private val uploadDir: String
) : FileStorageProvider {
    init {
        val root = Paths.get(uploadDir)
        if (!Files.exists(root)) {
            Files.createDirectories(root)
        }
    }

    override fun store(inputStream: InputStream, storagePath: String): String {
        val targetPath = Paths.get(uploadDir).resolve(storagePath)
        
        // 디렉토리가 없으면 생성
        Files.createDirectories(targetPath.parent)
        
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
        return storagePath
    }

    override fun load(storagePath: String): InputStream {
        val targetPath = Paths.get(uploadDir).resolve(storagePath)
        if (!Files.exists(targetPath)) {
            throw IllegalArgumentException("File not found at: $storagePath")
        }
        return Files.newInputStream(targetPath)
    }

    override fun delete(storagePath: String) {
        val targetPath = Paths.get(uploadDir).resolve(storagePath)
        Files.deleteIfExists(targetPath)
    }
}
