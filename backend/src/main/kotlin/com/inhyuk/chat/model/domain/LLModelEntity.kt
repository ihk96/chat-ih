package com.inhyuk.chat.model.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "ll_models")
@EntityListeners(AuditingEntityListener::class)
class LLModelEntity(

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    val id : String? = null,
    var publicName : String,
    var originName : String,
    var providerId: String, // Links to AiProvider
    var completionUrl : String,

    var isCustomSystemPrompt : Boolean = false,
    var systemPromptId : Long? = null

) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}