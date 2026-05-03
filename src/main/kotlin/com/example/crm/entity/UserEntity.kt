package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "\"user\"")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "person_id")
    var personId: Long? = null,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 255)
    var email: String = "",

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String = "",

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
