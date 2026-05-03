package com.example.crm.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class CustomerResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val personId: Long?,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val document: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val photo: String? = null,
    val physical: PersonPhysicalResponse? = null,
    val legal: PersonLegalResponse? = null,
    val contacts: List<ContactResponse> = emptyList(),
    val addresses: List<PersonAddressResponse> = emptyList()
)

data class CustomerSummaryResponse(
    val id: Long,
    val tenantId: Long,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val document: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val photo: String? = null
)
