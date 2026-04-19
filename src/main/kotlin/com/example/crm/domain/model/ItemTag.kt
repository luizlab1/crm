package com.example.crm.domain.model

import java.time.OffsetDateTime

data class ItemTag(
    val id: Long = 0,
    val itemId: Long = 0,
    val tag: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
