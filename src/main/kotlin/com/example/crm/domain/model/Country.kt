package com.example.crm.domain.model

import java.time.OffsetDateTime

data class Country(
    val id: Long = 0,
    val iso2: String,
    val iso3: String,
    val country: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

