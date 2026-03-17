package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Schedule(
    val id: Long = 0,
    val code: UUID = UUID.randomUUID(),
    val tenantId: Long,
    val customerId: Long,
    val appointmentId: Long,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

