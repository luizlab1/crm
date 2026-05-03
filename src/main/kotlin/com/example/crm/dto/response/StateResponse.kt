package com.example.crm.dto.response

import java.time.OffsetDateTime

data class StateResponse(
    val id: Long,
    val countryId: Long,
    val acronym: String,
    val state: String,
    val ibgeCode: Int?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
