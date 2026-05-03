package com.example.crm.dto.response

import com.example.crm.entity.ItemType
import java.time.OffsetDateTime

data class ItemCategoryResponse(
    val id: Long,
    val tenantId: Long,
    val name: String,
    val description: String?,
    val showOnSite: Boolean,
    val sortOrder: Int,
    val active: Boolean,
    val availableTypes: Set<ItemType>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val photo: String? = null
)

data class ItemCategoryListResponse(
    val id: Long,
    val tenantId: Long,
    val name: String,
    val description: String?,
    val showOnSite: Boolean,
    val sortOrder: Int,
    val active: Boolean,
    val availableTypes: Set<ItemType>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val photo: String? = null
)
