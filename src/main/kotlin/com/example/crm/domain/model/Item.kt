package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Item(
    val id: Long = 0,
    val code: UUID = UUID.randomUUID(),
    val tenantId: Long,
    val categoryId: Long? = null,
    val type: ItemType,
    val name: String,
    val sku: String? = null,
    val isActive: Boolean = true,
    val productDatasheet: ItemProductDatasheet? = null,
    val serviceDatasheet: ItemServiceDatasheet? = null,
    val images: List<ItemImage> = emptyList(),
    val tags: List<ItemTag> = emptyList(),
    val options: List<ItemOption> = emptyList(),
    val additionals: List<ItemAdditional> = emptyList(),
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

