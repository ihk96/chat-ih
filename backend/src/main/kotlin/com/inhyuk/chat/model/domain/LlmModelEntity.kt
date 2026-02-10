package com.inhyuk.chat.model.domain

import com.inhyuk.chat.common.jpa.JsonMapConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "ll_models")
@EntityListeners(AuditingEntityListener::class)
class LlmModelEntity(
    @Id
    val id: String,

    @Column(name = "provider_id", nullable = false)
    var providerId: String,

    @Column(name = "origin_name", nullable = false)
    var originName: String,

    @Column(name = "public_name", nullable = false)
    var publicName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ModelStatus,

    @Convert(converter = JsonMapConverter::class)
    @Column(name = "extra_config", columnDefinition = "TEXT")
    var extraConfig: Map<String, Any>? = null
) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}
