package com.inhyuk.chat.file.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface FileRepository : JpaRepository<FileEntity, String> {
    fun findAllByIsUsedFalseAndCreatedDateBefore(dateTime: LocalDateTime): List<FileEntity>
}
