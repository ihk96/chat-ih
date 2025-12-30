package com.inhyuk.chat.file.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.file.api.dto.FileResponseDto
import com.inhyuk.chat.file.api.dto.FileUploadResponseDto
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.net.URLEncoder

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    private val fileUsecase: FileUsecase
) {

    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        authentication: Authentication
    ): RestResponse<FileUploadResponseDto> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        
        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty")
        }

        val responseDto = fileUsecase.uploadFile(
            inputStream = file.inputStream,
            originalFileName = file.originalFilename ?: "unknown",
            contentType = file.contentType ?: "application/octet-stream",
            size = file.size,
            userId = userId
        )

        return RestResponse.ok(responseDto)
    }

    @GetMapping("/{fileId}")
    fun getFile(
        @PathVariable fileId: String,
        authentication: Authentication
    ): RestResponse<FileResponseDto> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val responseDto = fileUsecase.getFile(fileId, userId)
        return RestResponse.ok(responseDto)
    }

    @GetMapping("/{fileId}/download")
    fun downloadFile(
        @PathVariable fileId: String,
        authentication: Authentication
    ): ResponseEntity<Resource> {
        val userId = authentication.principal as? String ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val downloadDto = fileUsecase.downloadFile(fileId, userId)
        
        val resource = InputStreamResource(downloadDto.inputStream)
        val fileName = URLEncoder.encode(downloadDto.fileName, "UTF-8").replace("+", "%20")
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(downloadDto.mimeType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(resource)
    }
}
