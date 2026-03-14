package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class ItemCategoryResponse(
    val id: Long,
    val tenantId: Long,
    val name: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

