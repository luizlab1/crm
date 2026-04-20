package com.example.crm.domain.model

import java.time.OffsetDateTime

data class ItemCategory(
    val id: Long = 0,
    val tenantId: Long,
    val name: String,
    val description: String? = null,
    val showOnSite: Boolean = true,
    val availableTypes: Set<ItemType> = ItemType.entries.toSet(),
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

