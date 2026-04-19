package com.example.crm.domain.model

import java.time.OffsetDateTime

data class ItemAdditional(
    val id: Long = 0,
    val itemId: Long = 0,
    val name: String,
    val priceCents: Long = 0,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
