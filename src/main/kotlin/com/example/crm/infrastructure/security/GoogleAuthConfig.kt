package com.example.crm.infrastructure.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.google")
data class GoogleAuthConfig(
    val clientId: String = ""
)
