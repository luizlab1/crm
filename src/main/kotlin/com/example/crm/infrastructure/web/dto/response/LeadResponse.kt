package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class LeadResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val flowId: Long,
    val customerId: Long?,
    val status: String,
    val source: String?,
    val estimatedValueCents: Long?,
    val notes: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class LeadMessageResponse(
    val id: Long,
    val leadId: Long,
    val message: String,
    val channel: String?,
    val createdByUserId: Long?,
    val createdAt: OffsetDateTime
)

