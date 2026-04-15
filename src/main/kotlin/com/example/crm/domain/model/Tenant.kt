package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Tenant(
    val id: Long = 0,
    val parentTenantId: Long? = null,
    val code: UUID = UUID.randomUUID(),
    val name: String,
    val category: String,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    val person: Person? = null,
    val addresses: List<PersonAddress> = emptyList()
)

