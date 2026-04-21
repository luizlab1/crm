package com.example.crm.infrastructure.web.dto.response

import com.example.crm.domain.model.ItemType
import java.time.OffsetDateTime

data class ItemCategoryResponse(
    val id: Long,
    val tenantId: Long,
    val name: String,
    val description: String?,
    val showOnSite: Boolean,
    val sortOrder: Int,
    val isActive: Boolean,
    val availableTypes: Set<ItemType>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val photo: String? = null
)

