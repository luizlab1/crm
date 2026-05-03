package com.example.crm.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "\"order\"")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "customer_id", nullable = false)
    var customerId: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(nullable = false, length = 30)
    var status: String = "DRAFT",

    @Column(name = "subtotal_cents", nullable = false)
    var subtotalCents: Long = 0,

    @Column(name = "discount_cents", nullable = false)
    var discountCents: Long = 0,

    @Column(name = "total_cents", nullable = false)
    var totalCents: Long = 0,

    @Column(name = "currency_code", nullable = false, length = 3)
    var currencyCode: String = "BRL",

    @Column(length = 1000)
    var notes: String? = null,

    @OneToMany(mappedBy = "orderId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItemEntity> = mutableListOf()
) : BaseEntity()
