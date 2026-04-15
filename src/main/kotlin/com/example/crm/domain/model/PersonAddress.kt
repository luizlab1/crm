package com.example.crm.domain.model

import java.time.OffsetDateTime

data class PersonAddress(
    val address: Address,
    val type: String = "RESIDENTIAL",
    val isPrimary: Boolean = false,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
