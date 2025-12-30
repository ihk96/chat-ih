package com.inhyuk.chat.file.facade

import com.inhyuk.chat.file.domain.FileRepository
import com.inhyuk.chat.file.domain.FileService
import com.inhyuk.chat.file.facade.dto.FileDTO
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream

@Component
class FileFacade(
    private val fileService: FileService,
    private val fileRepository: FileRepository
) {

    @Transactional
    fun uploadFile(inputStream: InputStream, originalFileName: String, contentType: String, size: Long, userId: String): FileDTO {
        val fileEntity = fileService.uploadFile(inputStream, originalFileName, contentType, size, userId)
        return FileDTO(fileEntity)
    }

    @Transactional(readOnly = true)
    fun getFile(fileId: String): FileDTO {
        val fileEntity = fileRepository.findById(fileId).orElseThrow { IllegalArgumentException("File not found") }
        return FileDTO(fileEntity)
    }

    @Transactional(readOnly = true)
    fun getFiles(fileIds: List<String>): List<FileDTO> {
        if(fileIds.isEmpty()) return emptyList()

        return fileIds.chunked(20).flatMap { chunk->
            val fileEntities = fileRepository.findAllById(chunk)
            fileEntities.map { FileDTO(it) }
        }
    }

    @Transactional(readOnly = true)
    fun getFileStream(fileId: String): InputStream {
        val fileEntity = fileRepository.findById(fileId).orElseThrow { IllegalArgumentException("File not found") }
        return fileService.getFileStream(fileEntity.storagePath)
    }
}