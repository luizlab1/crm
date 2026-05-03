package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "customer")
class CustomerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "person_id")
    var personId: Long? = null,

    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String = "",

    @Column(length = 255)
    var email: String? = null,

    @Column(length = 30)
    var phone: String? = null,

    @Column(length = 30)
    var document: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
