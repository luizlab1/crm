package com.example.crm.infrastructure.web.dto.request

data class WorkerRequest(
    val tenantId: Long,
    val userId: Long? = null,
    val isActive: Boolean = true,
    // dados de pessoa — obrigatório fornecer physical ou legal
    val physical: PersonPhysicalRequest? = null,
    val legal: PersonLegalRequest? = null,
    val contacts: List<ContactRequest> = emptyList()
)
