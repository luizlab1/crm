package com.example.crm.infrastructure.web.dto.request

import com.example.crm.domain.model.ItemType

data class ItemCategoryPatchRequest(
    val tenantId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val showOnSite: Boolean? = null,
    val sortOrder: Int? = null,
    val availableTypes: Set<ItemType>? = null
)
