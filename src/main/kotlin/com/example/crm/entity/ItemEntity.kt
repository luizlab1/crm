package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import java.util.UUID

@Entity
@Table(name = "item")
class ItemEntity(
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
    @Enumerated(EnumType.STRING)
    var type: ItemType = ItemType.PRODUCT,

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(length = 100)
    var sku: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
