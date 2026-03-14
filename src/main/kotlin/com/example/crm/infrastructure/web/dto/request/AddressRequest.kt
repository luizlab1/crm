package com.example.crm.infrastructure.web.dto.request

import java.math.BigDecimal

data class AddressRequest(
    val street: String,
    val number: String? = null,
    val complement: String? = null,
    val neighborhood: String,
    val cityId: Long,
    val postalCode: String,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val isActive: Boolean = true
)

