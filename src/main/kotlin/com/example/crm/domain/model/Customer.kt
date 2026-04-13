package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Customer(
    val id: Long = 0,
    val code: UUID = UUID.randomUUID(),
    val tenantId: Long,
    val personId: Long? = null,
    val fullName: String,
    val email: String? = null,
    val phone: String? = null,
    val document: String? = null,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    // dados de pessoa embutidos (gerenciados internamente pelo use case)
    val person: Person? = null
)

