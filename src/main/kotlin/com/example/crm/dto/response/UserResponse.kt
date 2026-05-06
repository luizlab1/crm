package com.example.crm.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class UserResponse(
    val id: Long,
    val tenantId: Long,
    val personId: Long?,
    val code: UUID,
    val email: String,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val photo: String? = null,
    val roles: List<RoleResponse> = emptyList(),
    val physical: PersonPhysicalResponse? = null,
    val legal: PersonLegalResponse? = null,
    val contacts: List<ContactResponse> = emptyList(),
    val addresses: List<PersonAddressResponse> = emptyList()
)

data class UserSummaryResponse(
    val id: Long,
    val tenantId: Long,
    val email: String,
    val name: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val photo: String? = null,
    val roles: List<RoleResponse> = emptyList()
)
