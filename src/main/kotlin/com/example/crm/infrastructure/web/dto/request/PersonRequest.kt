package com.example.crm.infrastructure.web.dto.request

import java.time.LocalDate

data class PersonRequest(
    val tenantId: Long,
    val isActive: Boolean = true,
    val physical: PersonPhysicalRequest? = null,
    val legal: PersonLegalRequest? = null,
    val contacts: List<ContactRequest> = emptyList()
)

data class PersonPhysicalRequest(
    val fullName: String,
    val cpf: String,
    val birthDate: LocalDate? = null
)

data class PersonLegalRequest(
    val corporateName: String,
    val tradeName: String? = null,
    val cnpj: String
)

data class ContactRequest(
    val type: String,
    val contactValue: String,
    val isPrimary: Boolean = false,
    val isActive: Boolean = true
)

