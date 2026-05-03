package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "item_option")
class ItemOptionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "item_id", nullable = false)
    var itemId: Long = 0,

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(name = "price_delta_cents", nullable = false)
    var priceDeltaCents: Long = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
