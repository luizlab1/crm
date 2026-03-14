package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class RoleResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

