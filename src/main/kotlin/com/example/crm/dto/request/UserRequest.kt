package com.example.crm.dto.request

data class UserRequest(
    val tenantId: Long,
    val email: String,
    val passwordHash: String,
    val isActive: Boolean = true,
    val physical: PersonPhysicalRequest? = null,
    val legal: PersonLegalRequest? = null,
    val contacts: List<ContactRequest> = emptyList(),
    val addresses: List<PersonAddressRequest> = emptyList()
)
