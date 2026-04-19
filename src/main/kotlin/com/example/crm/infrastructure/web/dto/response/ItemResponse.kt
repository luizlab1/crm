package com.example.crm.infrastructure.web.dto.response

import com.example.crm.domain.model.ItemType
import java.time.OffsetDateTime
import java.util.UUID

data class ItemResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val categoryId: Long?,
    val type: ItemType,
    val name: String,
    val sku: String?,
    val isActive: Boolean,
    val photos: List<String> = emptyList(),
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

