package com.inhyuk.chat.file.api

import com.inhyuk.chat.file.api.dto.FileDownloadDto
import com.inhyuk.chat.file.api.dto.FileResponseDto
import com.inhyuk.chat.file.api.dto.FileUploadResponseDto
import com.inhyuk.chat.file.domain.FileRepository
import com.inhyuk.chat.file.domain.FileService
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class FileUsecase(
    private val fileService: FileService,
    private val fileRepository: FileRepository
) {
    fun uploadFile(
        inputStream: InputStream,
        originalFileName: String,
        contentType: String,
        size: Long,
        userId: String
    ): FileUploadResponseDto {
        val fileEntity = fileService.uploadFile(
            inputStream = inputStream,
            originalFileName = originalFileName,
            mimeType = contentType,
            size = size,
            userId = userId
        )

        return FileUploadResponseDto(
            id = fileEntity.id!!,
            fileName = fileEntity.originalFileName,
            size = fileEntity.size,
            mimeType = fileEntity.mimeType,
        )
    }

    fun getFile(fileId: String, userId: String): FileResponseDto {
        val fileEntity = fileRepository.findById(fileId).orElseThrow { IllegalArgumentException("File not found") }
        
        // 간단한 권한 체크 (추후 확장 가능)
        if (fileEntity.userId != userId) {
            throw IllegalArgumentException("Access denied for file: $fileId")
        }

        return FileResponseDto(
            id = fileEntity.id!!,
            fileName = fileEntity.originalFileName,
            size = fileEntity.size,
            mimeType = fileEntity.mimeType,
            userId = fileEntity.userId,
            isUsed = fileEntity.isUsed
        )
    }

    fun downloadFile(fileId: String, userId: String): FileDownloadDto {
        val fileEntity = fileRepository.findById(fileId).orElseThrow { IllegalArgumentException("File not found") }

        // 간단한 권한 체크
        if (fileEntity.userId != userId) {
            throw IllegalArgumentException("Access denied for file: $fileId")
        }

        val inputStream = fileService.getFileStream(fileEntity.storagePath)

        return FileDownloadDto(
            fileName = fileEntity.originalFileName,
            mimeType = fileEntity.mimeType,
            inputStream = inputStream
        )
    }
}
