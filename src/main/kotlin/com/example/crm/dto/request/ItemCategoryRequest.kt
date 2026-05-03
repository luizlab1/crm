package com.example.crm.dto.request

import com.example.crm.entity.ItemType

data class ItemCategoryRequest(
    val tenantId: Long,
    val name: String,
    val description: String? = null,
    val showOnSite: Boolean = true,
    val sortOrder: Int? = 0,
    val active: Boolean = true,
    val availableTypes: Set<ItemType> = ItemType.entries.toSet()
)

data class ItemCategoryPatchRequest(
    val tenantId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val showOnSite: Boolean? = null,
    val sortOrder: Int? = null,
    val active: Boolean? = null,
    val availableTypes: Set<ItemType>? = null
)

data class ItemCategorySortOrderEntry(
    val id: Long,
    val sortOrder: Int
)

data class ItemCategorySortOrderRequest(
    val items: List<ItemCategorySortOrderEntry>
)
