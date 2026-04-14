package com.example.crm.infrastructure.web.dto.request

import java.time.LocalDate

data class CustomerRequest(
    val tenantId: Long,
    val fullName: String,
    val email: String? = null,
    val phone: String? = null,
    val document: String? = null,
    val isActive: Boolean = true,
    // dados de pessoa — quando fornecidos, a pessoa é criada/atualizada automaticamente
    val physical: PersonPhysicalRequest? = null,
    val legal: PersonLegalRequest? = null,
    val contacts: List<ContactRequest> = emptyList(),
    val address: AddressRequest? = null
)
