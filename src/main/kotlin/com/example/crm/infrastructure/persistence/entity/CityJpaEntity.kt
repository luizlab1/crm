package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "city")
class CityJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "state_id", nullable = false)
    var stateId: Long = 0,

    @Column(nullable = false, length = 150)
    var city: String = "",

    @Column(name = "ibge_code")
    var ibgeCode: Int? = null
) : BaseJpaEntity()

