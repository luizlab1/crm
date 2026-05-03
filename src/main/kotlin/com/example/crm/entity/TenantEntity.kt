package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "tenant")
class TenantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "parent_tenant_id")
    var parentTenantId: Long? = null,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 120)
    var name: String = "",

    @Column(nullable = false, length = 60)
    var category: String = "",

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
