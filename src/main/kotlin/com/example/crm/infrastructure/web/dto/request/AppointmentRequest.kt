package com.example.crm.infrastructure.web.dto.request

import java.time.OffsetDateTime

data class AppointmentRequest(
    val status: String = "SCHEDULED",
    val scheduledAt: OffsetDateTime,
    val startedAt: OffsetDateTime? = null,
    val finishedAt: OffsetDateTime? = null,
    val totalCents: Long? = null,
    val notes: String? = null
)

