package com.example.crm.domain.model

import java.time.OffsetDateTime

data class Permission(
    val id: Long = 0,
    val code: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

