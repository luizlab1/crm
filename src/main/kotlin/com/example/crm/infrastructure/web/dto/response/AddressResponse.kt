package com.example.crm.infrastructure.web.dto.response

import java.math.BigDecimal
import java.time.OffsetDateTime

data class AddressResponse(
    val id: Long,
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

