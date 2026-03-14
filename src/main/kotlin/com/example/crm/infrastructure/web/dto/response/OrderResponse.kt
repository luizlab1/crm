package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class OrderResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val customerId: Long,
    val userId: Long,
    val status: String,
    val subtotalCents: Long,
    val discountCents: Long,
    val totalCents: Long,
    val currencyCode: String,
    val notes: String?,
    val items: List<OrderItemResponse>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class OrderItemResponse(
    val id: Long,
    val itemId: Long,
    val quantity: Int,
    val unitPriceCents: Long,
    val totalPriceCents: Long,
    val createdAt: OffsetDateTime
)

