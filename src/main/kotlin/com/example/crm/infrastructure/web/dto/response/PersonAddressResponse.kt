package com.example.crm.infrastructure.web.dto.response

import com.example.crm.infrastructure.web.dto.request.PersonAddressType
import java.math.BigDecimal
import java.time.OffsetDateTime

data class PersonAddressResponse(
    val id: Long,
    val type: PersonAddressType,
    val isPrimary: Boolean,
    val street: String,
    val number: String?,
    val complement: String?,
    val neighborhood: String,
    val cityId: Long,
    val postalCode: String,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
