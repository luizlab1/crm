package com.example.crm.domain.model

import java.time.OffsetDateTime

data class State(
    val id: Long = 0,
    val countryId: Long,
    val acronym: String,
    val state: String,
    val ibgeCode: Int? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

