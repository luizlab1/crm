package com.example.crm.dto.request

data class WorkerRequest(
    val tenantId: Long,
    val userId: Long? = null,
    val isActive: Boolean = true,
    val physical: PersonPhysicalRequest? = null,
    val legal: PersonLegalRequest? = null,
    val contacts: List<ContactRequest> = emptyList(),
    val addresses: List<PersonAddressRequest> = emptyList()
)
