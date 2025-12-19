package com.inhyuk.chat.common.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${security.jwt.secret}")
    private val secret: String,
    @Value("\${security.jwt.expiration-millis:86400000}")
    private val expirationMillis: Long
) {

    private val key: SecretKey by lazy {
        val bytes = Decoders.BASE64.decode(secret)
        Keys.hmacShaKeyFor(bytes)
    }

    fun generateToken(userId: String, username: String, roles: String): String {
        val now = Date()
        val expiry = Date(now.time + expirationMillis)
        return Jwts.builder()
            .setSubject(userId)
            .claim("username", username)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validate(token: String): Boolean = try {
        parseClaims(token)
        true
    } catch (e: Exception) {
        false
    }

    fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
