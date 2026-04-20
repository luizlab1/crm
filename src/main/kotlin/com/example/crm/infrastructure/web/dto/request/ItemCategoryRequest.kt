package com.example.crm.infrastructure.web.dto.request

import com.example.crm.domain.model.ItemType

data class ItemCategoryRequest(
    val tenantId: Long,
    val name: String,
    val showOnSite: Boolean = true,
    val availableTypes: Set<ItemType> = ItemType.entries.toSet()
)

