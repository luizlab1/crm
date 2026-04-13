package com.example.crm.infrastructure.web.dto.request

data class UserRequest(
    val tenantId: Long,
    val email: String,
    val passwordHash: String,
    val isActive: Boolean = true,
    // dados de pessoa — quando fornecidos, a pessoa é criada/atualizada automaticamente
    val physical: PersonPhysicalRequest? = null,
    val legal: PersonLegalRequest? = null,
    val contacts: List<ContactRequest> = emptyList()
)
