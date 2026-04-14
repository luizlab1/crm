package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "person_address")
class PersonAddressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "person_id", nullable = false)
    var personId: Long = 0,

    @Column(name = "address_id", nullable = false)
    var addressId: Long = 0,

    @Column(nullable = false, length = 120)
    var type: String = "MAIN",

    @Column(name = "is_primary", nullable = false)
    var isPrimary: Boolean = true
) : BaseJpaEntity()
