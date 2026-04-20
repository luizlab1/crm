package com.example.crm.infrastructure.web.dto.request

data class ItemCategorySortOrderEntry(
    val id: Long,
    val sortOrder: Int
)

data class ItemCategorySortOrderRequest(
    val items: List<ItemCategorySortOrderEntry>
)
