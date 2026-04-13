package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class User(
    val id: Long = 0,
    val tenantId: Long,
    val personId: Long? = null,
    val code: UUID = UUID.randomUUID(),
    val email: String,
    val passwordHash: String,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    // dados de pessoa embutidos (gerenciados internamente pelo use case)
    val person: Person? = null
)

