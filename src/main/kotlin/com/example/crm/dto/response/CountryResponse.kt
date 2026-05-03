package com.example.crm.dto.response

import java.time.OffsetDateTime

data class CountryResponse(
    val id: Long,
    val iso2: String,
    val iso3: String,
    val country: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
