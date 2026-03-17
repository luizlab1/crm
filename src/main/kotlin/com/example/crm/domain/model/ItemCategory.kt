package com.example.crm.domain.model

import java.time.OffsetDateTime

data class ItemCategory(
    val id: Long = 0,
    val tenantId: Long,
    val name: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

