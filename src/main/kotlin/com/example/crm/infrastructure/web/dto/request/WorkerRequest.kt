package com.example.crm.infrastructure.web.dto.request

data class WorkerRequest(
    val tenantId: Long,
    val personId: Long,
    val userId: Long? = null,
    val isActive: Boolean = true
)

