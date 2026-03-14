package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "permission")
class PermissionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 80)
    var code: String = "",

    @Column(length = 255)
    var description: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseJpaEntity()

