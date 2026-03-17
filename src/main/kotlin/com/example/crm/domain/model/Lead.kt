package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Lead(
    val id: Long = 0,
    val code: UUID = UUID.randomUUID(),
    val tenantId: Long,
    val flowId: Long,
    val customerId: Long? = null,
    val status: String = "NEW",
    val source: String? = null,
    val estimatedValueCents: Long? = null,
    val notes: String? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

data class LeadMessage(
    val id: Long = 0,
    val leadId: Long,
    val message: String,
    val channel: String? = null,
    val createdByUserId: Long? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

