package com.example.crm.domain.model

import java.time.OffsetDateTime

data class ItemOption(
    val id: Long = 0,
    val itemId: Long = 0,
    val name: String,
    val priceDeltaCents: Long = 0,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
