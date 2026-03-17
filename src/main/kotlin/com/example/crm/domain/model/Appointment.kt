package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Appointment(
    val id: Long = 0,
    val code: UUID = UUID.randomUUID(),
    val status: String = "SCHEDULED",
    val scheduledAt: OffsetDateTime,
    val startedAt: OffsetDateTime? = null,
    val finishedAt: OffsetDateTime? = null,
    val totalCents: Long? = null,
    val notes: String? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

