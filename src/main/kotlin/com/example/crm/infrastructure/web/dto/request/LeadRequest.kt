package com.example.crm.infrastructure.web.dto.request

data class LeadRequest(
    val tenantId: Long,
    val flowId: Long,
    val customerId: Long? = null,
    val status: String = "NEW",
    val source: String? = null,
    val estimatedValueCents: Long? = null,
    val notes: String? = null
)

data class LeadMessageRequest(
    val message: String,
    val channel: String? = null,
    val createdByUserId: Long? = null
)

