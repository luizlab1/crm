package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class WorkerResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val personId: Long,
    val userId: Long?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val photo: String? = null,
    // dados de pessoa embutidos
    val physical: PersonPhysicalResponse? = null,
    val legal: PersonLegalResponse? = null,
    val contacts: List<ContactResponse> = emptyList(),
    val addresses: List<PersonAddressResponse> = emptyList()
)
