package com.example.crm.domain.model

import java.time.OffsetDateTime

data class City(
    val id: Long = 0,
    val stateId: Long,
    val city: String,
    val ibgeCode: Int? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

