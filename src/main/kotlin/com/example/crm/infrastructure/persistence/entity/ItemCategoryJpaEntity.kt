package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "item_category")
class ItemCategoryJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(nullable = false, length = 150)
    var name: String = ""
) : BaseJpaEntity()

