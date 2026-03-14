package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "item")
class ItemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "category_id")
    var categoryId: Long? = null,

    @Column(nullable = false, length = 60)
    var type: String = "",

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(length = 100)
    var sku: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseJpaEntity()

