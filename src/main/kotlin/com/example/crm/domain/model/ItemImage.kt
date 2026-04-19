package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class ItemImage(
    val id: Long = 0,
    val code: UUID = UUID.randomUUID(),
    val itemId: Long = 0,
    val url: String,
    val altText: String? = null,
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
