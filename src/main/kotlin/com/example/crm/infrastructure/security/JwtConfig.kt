package com.example.crm.infrastructure.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.jwt")
data class JwtConfig(
    // Use a sufficiently long secret (>= 256 bits). This default is for local/dev only.
    val secret: String = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
    val expirationMs: Long = 3600000 // 1h
)
