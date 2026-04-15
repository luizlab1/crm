package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class UserSummaryResponse(
    val id: Long,
    val tenantId: Long,
    val email: String,
    val name: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime
)
