package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "item_product_datasheet")
class ItemProductDatasheetJpaEntity(
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

    @Column(name = "unit_of_measure_id")
    var unitOfMeasureId: Long? = null,

    @Column(name = "weight_kg", precision = 12, scale = 3)
    var weightKg: BigDecimal? = null,

    @Column(name = "volume_m3", precision = 12, scale = 6)
    var volumeM3: BigDecimal? = null,

    @Column(name = "density_kg_m3", precision = 12, scale = 3)
    var densityKgM3: BigDecimal? = null,

    @Column(name = "height_cm", precision = 12, scale = 3)
    var heightCm: BigDecimal? = null,

    @Column(name = "width_cm", precision = 12, scale = 3)
    var widthCm: BigDecimal? = null,

    @Column(name = "length_cm", precision = 12, scale = 3)
    var lengthCm: BigDecimal? = null
) : BaseJpaEntity()
