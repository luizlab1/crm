package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class PermissionResponse(
    val id: Long,
    val code: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

