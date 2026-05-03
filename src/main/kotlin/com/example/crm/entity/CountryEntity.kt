package com.example.crm.entity

import jakarta.persistence.*

@Entity
@Table(name = "country")
class CountryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 2)
    var iso2: String = "",

    @Column(nullable = false, length = 3)
    var iso3: String = "",

    @Column(nullable = false, length = 100)
    var country: String = ""
) : BaseEntity()
