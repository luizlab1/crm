package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class CityResponse(
    val id: Long,
    val stateId: Long,
    val city: String,
    val ibgeCode: Int?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

