package com.example.crm.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class ScheduleResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val customerId: Long,
    val appointmentId: Long,
    val description: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
