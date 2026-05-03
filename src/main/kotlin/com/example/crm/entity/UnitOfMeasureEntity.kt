package com.example.crm.entity

import jakarta.persistence.*

@Entity
@Table(name = "unit_of_measure")
class UnitOfMeasureEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 20)
    var code: String = "",

    @Column(nullable = false, length = 100)
    var name: String = "",

    @Column(length = 10)
    var symbol: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
