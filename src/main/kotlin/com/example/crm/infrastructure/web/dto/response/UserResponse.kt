package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class UserResponse(
    val id: Long,
    val tenantId: Long,
    val personId: Long?,
    val code: UUID,
    val email: String,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

