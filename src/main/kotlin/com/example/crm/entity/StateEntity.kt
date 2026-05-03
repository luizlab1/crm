package com.example.crm.entity

import jakarta.persistence.*

@Entity
@Table(name = "state")
class StateEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "country_id", nullable = false)
    var countryId: Long = 0,

    @Column(nullable = false, length = 2)
    var acronym: String = "",

    @Column(nullable = false, length = 100)
    var state: String = "",

    @Column(name = "ibge_code")
    var ibgeCode: Int? = null
) : BaseEntity()
