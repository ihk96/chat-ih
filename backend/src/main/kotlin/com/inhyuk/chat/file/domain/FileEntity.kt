package com.inhyuk.chat.file.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "files")
@EntityListeners(AuditingEntityListener::class)
class FileEntity(
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    val id: String? = null,

    @Column(nullable = false)
    val originalFileName: String,

    @Column(nullable = false)
    val storagePath: String,

    @Column(nullable = false)
    val storedName: String,

    @Column(nullable = false)
    val size: Long,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    var isUsed: Boolean = false,

    @Column(nullable = false)
    val mimeType: String,

) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null

    fun markAsUsed() {
        this.isUsed = true
    }
}