package com.inhyuk.chat.common.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
@Converter
class StringCryptoConverter : AttributeConverter<String, String> {

    @Value("\${security.encryption.key:12345678901234567890123456789012}") // 32 characters
    private lateinit var key: String

    private val algorithm = "AES"

    override fun convertToDatabaseColumn(attribute: String?): String? {
        if (attribute == null) return null
        return try {
            val keySpec = SecretKeySpec(key.toByteArray(), algorithm)
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            Base64.getEncoder().encodeToString(cipher.doFinal(attribute.toByteArray()))
        } catch (e: Exception) {
            throw RuntimeException("Error encrypting data", e)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if (dbData == null) return null
        return try {
            val keySpec = SecretKeySpec(key.toByteArray(), algorithm)
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            String(cipher.doFinal(Base64.getDecoder().decode(dbData)))
        } catch (e: Exception) {
            // Decryption failed (possibly plain text data from before migration)
            // In a real migration scenario, we might handle this gracefully or log it.
            // For now, return as is or throw. Returning as is helps if mixed data exists.
            dbData
        }
    }
}
