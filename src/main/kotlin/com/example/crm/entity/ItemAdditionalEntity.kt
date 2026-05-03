package com.example.crm.entity

import jakarta.persistence.*

@Entity
@Table(name = "item_additional")
class ItemAdditionalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "item_id", nullable = false)
    var itemId: Long = 0,

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(name = "price_cents", nullable = false)
    var priceCents: Long = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
