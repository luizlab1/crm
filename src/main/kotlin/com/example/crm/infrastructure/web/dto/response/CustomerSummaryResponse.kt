package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class CustomerSummaryResponse(
    val id: Long,
    val tenantId: Long,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val document: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime
)
