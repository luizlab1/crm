package com.example.crm.infrastructure.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService(private val config: JwtConfig) {
    private val key: Key = Keys.hmacShaKeyFor(config.secret.toByteArray())

    fun generateToken(subject: String, claims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val exp = Date(now.time + config.expirationMs)
        return Jwts.builder()
            .setSubject(subject)
            .addClaims(claims)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun parseSubject(token: String): String = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .body.subject
}
