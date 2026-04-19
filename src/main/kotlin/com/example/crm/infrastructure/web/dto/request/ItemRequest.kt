package com.example.crm.infrastructure.web.dto.request

import com.example.crm.domain.model.ItemType

data class ItemRequest(
    val tenantId: Long,
    val categoryId: Long? = null,
    val type: ItemType,
    val name: String,
    val sku: String? = null,
    val isActive: Boolean = true
)

