package com.inhyuk.chat.provider.domain

import com.inhyuk.chat.common.jpa.JsonMapConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "ai_providers",
)
@EntityListeners(AuditingEntityListener::class)
class AiProviderEntity(
    @Id
    val id: String,

    @Column(nullable = false, unique = true)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    var type: ProviderType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProviderStatus,

    @Column(name = "base_url")
    var baseUrl: String? = null,

    @Column(name = "api_key", nullable = false)
    var apiKey: String,

    @Convert(converter = JsonMapConverter::class)
    @Column(name = "extra_config", columnDefinition = "TEXT")
    var extraConfig: Map<String, Any>? = null
) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}
