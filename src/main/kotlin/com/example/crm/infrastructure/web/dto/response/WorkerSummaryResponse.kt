package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class WorkerSummaryResponse(
    val id: Long,
    val tenantId: Long,
    val name: String?,
    val document: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val photo: String? = null
)
