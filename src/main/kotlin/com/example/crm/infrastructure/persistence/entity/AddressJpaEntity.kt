package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "address")
class AddressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 150)
    var street: String = "",

    @Column(length = 20)
    var number: String? = null,

    @Column(length = 100)
    var complement: String? = null,

    @Column(nullable = false, length = 100)
    var neighborhood: String = "",

    @Column(name = "id_city", nullable = false)
    var cityId: Long = 0,

    @Column(name = "postal_code", nullable = false, length = 20)
    var postalCode: String = "",

    @Column(precision = 10, scale = 7)
    var latitude: BigDecimal? = null,

    @Column(precision = 10, scale = 7)
    var longitude: BigDecimal? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseJpaEntity()

