package com.example.crm.infrastructure.web.dto.request

data class CustomerRequest(
    val tenantId: Long,
    val personId: Long? = null,
    val fullName: String,
    val email: String? = null,
    val phone: String? = null,
    val document: String? = null,
    val isActive: Boolean = true
)

