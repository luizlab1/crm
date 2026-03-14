package com.example.crm.infrastructure.web.dto.request

data class TenantRequest(
    val parentTenantId: Long? = null,
    val name: String,
    val category: String,
    val isActive: Boolean = true
)

