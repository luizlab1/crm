package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class UnitOfMeasureResponse(
    val id: Long,
    val code: String,
    val name: String,
    val symbol: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

