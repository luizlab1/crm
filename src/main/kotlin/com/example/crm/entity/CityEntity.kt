package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "city")
class CityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "state_id", nullable = false)
    var stateId: Long = 0,

    @Column(nullable = false, length = 150)
    var city: String = "",

    @Column(name = "ibge_code")
    var ibgeCode: Int? = null
) : BaseEntity()
