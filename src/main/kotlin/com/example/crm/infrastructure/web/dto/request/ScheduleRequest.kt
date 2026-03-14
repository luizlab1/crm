package com.example.crm.infrastructure.web.dto.request

data class ScheduleRequest(
    val tenantId: Long,
    val customerId: Long,
    val appointmentId: Long,
    val description: String? = null,
    val isActive: Boolean = true
)

