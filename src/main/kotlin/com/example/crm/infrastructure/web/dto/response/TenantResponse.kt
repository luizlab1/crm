package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class TenantResponse(
    val id: Long,
    val parentTenantId: Long?,
    val code: UUID,
    val name: String,
    val category: String,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

