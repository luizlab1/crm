package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "item_image")
class ItemImageJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "item_id", nullable = false)
    var itemId: Long = 0,

    @Column(nullable = false, length = 1000)
    var url: String = "",

    @Column(name = "alt_text", length = 255)
    var altText: String? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseJpaEntity()
