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

    val name: String, // User-friendly name, e.g. "My OpenAI", "Local Ollama"
    
    val provider: ModelProvider,

    val baseUrl: String?, // e.g. "https://api.openai.com/v1" or "http://localhost:11434/v1"

    @Convert(converter = StringCryptoConverter::class)
    val apiKey: String?, // Encrypted
) {
    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    val lastModifiedDate: LocalDateTime? = null
}