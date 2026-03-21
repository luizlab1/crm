package com.example.crm.infrastructure

import com.example.crm.infrastructure.security.JwtConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtConfig::class)
class ConfigPropertiesRegistrar
