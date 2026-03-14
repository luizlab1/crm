package com.example.crm.infrastructure.web.dto.request

data class UserRequest(
    val tenantId: Long,
    val personId: Long? = null,
    val email: String,
    val passwordHash: String,
    val isActive: Boolean = true
)

