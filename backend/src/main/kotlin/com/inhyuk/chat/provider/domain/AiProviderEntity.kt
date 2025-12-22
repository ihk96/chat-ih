package com.inhyuk.chat.provider.domain

import com.inhyuk.chat.common.util.StringCryptoConverter
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "ai_providers")
@EntityListeners(AuditingEntityListener::class)
class AiProviderEntity(
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    val id: String? = null,

    var name: String, // User-friendly name, e.g. "My OpenAI", "Local Ollama"
    
    var provider: ModelProvider,

    var baseUrl: String?, // e.g. "https://api.openai.com/v1" or "http://localhost:11434/v1"

    @Convert(converter = StringCryptoConverter::class)
    var apiKey: String?, // Encrypted
) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
}