package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Order(
    val id: Long = 0,
    val code: UUID = UUID.randomUUID(),
    val tenantId: Long,
    val customerId: Long,
    val userId: Long,
    val status: String = "DRAFT",
    val subtotalCents: Long = 0,
    val discountCents: Long = 0,
    val totalCents: Long = 0,
    val currencyCode: String = "BRL",
    val notes: String? = null,
    val items: List<OrderItem> = emptyList(),
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

data class OrderItem(
    val id: Long = 0,
    val itemId: Long,
    val quantity: Int = 1,
    val unitPriceCents: Long,
    val totalPriceCents: Long,
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

