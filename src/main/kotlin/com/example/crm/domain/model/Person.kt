package com.example.crm.domain.model

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class Person(
    val id: Long = 0,
    val tenantId: Long,
    val code: UUID = UUID.randomUUID(),
    val isActive: Boolean = true,
    val physical: PersonPhysical? = null,
    val legal: PersonLegal? = null,
    val contacts: List<Contact> = emptyList(),
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

data class PersonPhysical(
    val fullName: String,
    val cpf: String,
    val birthDate: LocalDate? = null
)

data class PersonLegal(
    val corporateName: String,
    val tradeName: String? = null,
    val cnpj: String
)

data class Contact(
    val id: Long = 0,
    val type: String,
    val contactValue: String,
    val isPrimary: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

