package com.example.crm.dto.request

data class OrderRequest(
    val tenantId: Long,
    val customerId: Long,
    val userId: Long,
    val status: String = "DRAFT",
    val subtotalCents: Long = 0,
    val discountCents: Long = 0,
    val totalCents: Long = 0,
    val currencyCode: String = "BRL",
    val notes: String? = null,
    val items: List<OrderItemRequest> = emptyList()
)

data class OrderItemRequest(
    val itemId: Long,
    val quantity: Int = 1,
    val unitPriceCents: Long,
    val totalPriceCents: Long
)
