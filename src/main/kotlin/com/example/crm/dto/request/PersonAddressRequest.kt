package com.example.crm.dto.request

import java.math.BigDecimal

enum class PersonAddressType {
    RESIDENTIAL,
    COMMERCIAL
}

data class PersonAddressRequest(
    val id: Long? = null,
    val type: PersonAddressType = PersonAddressType.RESIDENTIAL,
    val isPrimary: Boolean = false,
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
