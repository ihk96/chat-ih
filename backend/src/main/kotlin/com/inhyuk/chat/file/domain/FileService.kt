package com.inhyuk.chat.file.domain

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class FileService(
    private val fileRepository: FileRepository,
    private val fileStorageProvider: FileStorageProvider
) {
    private val logger = LoggerFactory.getLogger(FileService::class.java)

    @Transactional
    fun uploadFile(
        inputStream: InputStream,
        originalFileName: String,
        mimeType: String,
        size: Long,
        userId: String
    ): FileEntity {
        val storedName = UUID.randomUUID().toString()
        val extension = originalFileName.substringAfterLast('.', "")
        val monthPath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"))
        val storagePath = "$monthPath/$storedName${if (extension.isNotEmpty()) ".$extension" else ""}"

        fileStorageProvider.store(inputStream, storagePath)

        val fileEntity = FileEntity(
            originalFileName = originalFileName,
            storedName = storedName,
            storagePath = storagePath,
            mimeType = mimeType,
            size = size,
            userId = userId
        )

        return fileRepository.save(fileEntity)
    }

    fun getFileStream(storagePath: String): InputStream {
        return fileStorageProvider.load(storagePath)
    }

    @Transactional
    fun markAsUsed(fileId: String) {
        val fileEntity = fileRepository.findById(fileId).orElseThrow {
            IllegalArgumentException("File not found: $fileId")
        }
        fileEntity.markAsUsed()
    }

    @Transactional
    fun deleteUnusedFiles(before: LocalDateTime) {
        val unusedFiles = fileRepository.findAllByIsUsedFalseAndCreatedDateBefore(before)
        unusedFiles.forEach { file ->
            try {
                fileStorageProvider.delete(file.storagePath)
                fileRepository.delete(file)
            } catch (e: Exception) {
                logger.error("Failed to delete unused file: ${file.id}", e)
            }
        }
    }

}
