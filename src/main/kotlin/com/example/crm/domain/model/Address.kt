package com.example.crm.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class Address(
    val id: Long = 0,
    val street: String,
    val number: String? = null,
    val complement: String? = null,
    val neighborhood: String,
    val cityId: Long,
    val postalCode: String,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

