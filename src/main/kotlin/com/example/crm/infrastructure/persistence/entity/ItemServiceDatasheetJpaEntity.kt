package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "item_service_datasheet")
class ItemServiceDatasheetJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "item_id", nullable = false)
    var itemId: Long = 0,

    @Column(length = 1000)
    var description: String? = null,

    @Column(name = "unit_price_cents", nullable = false)
    var unitPriceCents: Long = 0,

    @Column(name = "currency_code", nullable = false, length = 3)
    var currencyCode: String = "BRL",

    @Column(name = "duration_minutes")
    var durationMinutes: Int? = null,

    @Column(name = "requires_staff", nullable = false)
    var requiresStaff: Boolean = false,

    @Column(name = "buffer_minutes")
    var bufferMinutes: Int? = null
) : BaseJpaEntity()
