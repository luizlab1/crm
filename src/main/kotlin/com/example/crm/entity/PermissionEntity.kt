package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "permission")
class PermissionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 80)
    var code: String = "",

    @Column(length = 255)
    var description: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
