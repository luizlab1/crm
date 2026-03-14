package com.example.crm.infrastructure.web.dto.request

data class ItemRequest(
    val tenantId: Long,
    val categoryId: Long? = null,
    val type: String,
    val name: String,
    val sku: String? = null,
    val isActive: Boolean = true
)

