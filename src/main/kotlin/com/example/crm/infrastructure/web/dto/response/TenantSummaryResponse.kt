package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class TenantSummaryResponse(
    val id: Long,
    val parentTenantId: Long?,
    val name: String,
    val category: String,
    val document: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val photo: String? = null
)
