package com.example.crm.domain.model

data class ItemCategoryPatch(
    val tenantId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val showOnSite: Boolean? = null,
    val sortOrder: Int? = null,
    val availableTypes: Set<ItemType>? = null
)
