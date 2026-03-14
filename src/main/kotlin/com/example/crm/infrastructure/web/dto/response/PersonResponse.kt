package com.example.crm.infrastructure.web.dto.response

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class PersonResponse(
    val id: Long,
    val tenantId: Long,
    val code: UUID,
    val isActive: Boolean,
    val physical: PersonPhysicalResponse?,
    val legal: PersonLegalResponse?,
    val contacts: List<ContactResponse>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class PersonPhysicalResponse(
    val fullName: String,
    val cpf: String,
    val birthDate: LocalDate?
)

data class PersonLegalResponse(
    val corporateName: String,
    val tradeName: String?,
    val cnpj: String
)

data class ContactResponse(
    val id: Long,
    val type: String,
    val contactValue: String,
    val isPrimary: Boolean,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

