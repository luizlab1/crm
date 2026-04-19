package com.example.crm.domain.model

import java.time.OffsetDateTime

data class ItemServiceDatasheet(
    val id: Long = 0,
    val itemId: Long = 0,
    val description: String? = null,
    val unitPriceCents: Long = 0,
    val currencyCode: String = "BRL",
    val durationMinutes: Int? = null,
    val requiresStaff: Boolean = false,
    val bufferMinutes: Int? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
