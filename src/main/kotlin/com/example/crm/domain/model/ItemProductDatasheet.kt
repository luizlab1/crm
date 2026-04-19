package com.example.crm.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ItemProductDatasheet(
    val id: Long = 0,
    val itemId: Long = 0,
    val description: String? = null,
    val unitPriceCents: Long = 0,
    val currencyCode: String = "BRL",
    val unitOfMeasureId: Long? = null,
    val weightKg: BigDecimal? = null,
    val volumeM3: BigDecimal? = null,
    val densityKgM3: BigDecimal? = null,
    val heightCm: BigDecimal? = null,
    val widthCm: BigDecimal? = null,
    val lengthCm: BigDecimal? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
