package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "\"role\"")
class RoleJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 60)
    var name: String = "",

    @Column(length = 255)
    var description: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseJpaEntity()

