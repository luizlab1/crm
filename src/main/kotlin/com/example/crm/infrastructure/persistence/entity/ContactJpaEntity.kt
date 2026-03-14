package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "contact")
class ContactJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "person_id", nullable = false)
    var personId: Long = 0,

    @Column(nullable = false, length = 30)
    var type: String = "",

    @Column(name = "contact_value", nullable = false, length = 255)
    var contactValue: String = "",

    @Column(name = "is_primary", nullable = false)
    var isPrimary: Boolean = false,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseJpaEntity()

