package com.inhyuk.chat.file.facade.dto

import com.inhyuk.chat.file.domain.FileEntity
import java.time.LocalDateTime

data class FileDTO(
    val id: String?,
    val fileName: String,
    val storedName : String,
    val originalFileName: String,
    val storagePath: String,
    val size: Long,
    val mimeType: String,
    val userId: String,
    val isUsed: Boolean,
    val createdDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime?,
){

    constructor(fileEntity: FileEntity) : this (
        id = fileEntity.id,
        fileName = fileEntity.originalFileName,
        storedName = fileEntity.storedName,
        originalFileName = fileEntity.originalFileName,
        storagePath = fileEntity.storagePath,
        size = fileEntity.size,
        mimeType = fileEntity.mimeType,
        userId = fileEntity.userId,
        isUsed = fileEntity.isUsed,
        createdDate = fileEntity.createdDate,
        lastModifiedDate = fileEntity.lastModifiedDate
    )


}