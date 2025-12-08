package com.inhyuk.chat.domain.connection

import com.inhyuk.chat.common.StringCryptoConverter
import com.inhyuk.chat.domain.connection.ModelProvider
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "model_provider_connection")
class ModelProviderConnection(
    @Id
    val id: String = UUID.randomUUID().toString(),

    val name: String, // User-friendly name, e.g. "My OpenAI", "Local Ollama"
    
    val provider: ModelProvider,

    val baseUrl: String, // e.g. "https://api.openai.com/v1" or "http://localhost:11434/v1"

    @Convert(converter = StringCryptoConverter::class)
    val apiKey: String, // Encrypted
)
