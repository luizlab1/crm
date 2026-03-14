package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class ItemResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val categoryId: Long?,
    val type: String,
    val name: String,
    val sku: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

