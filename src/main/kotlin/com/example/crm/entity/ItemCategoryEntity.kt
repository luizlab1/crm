package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.ElementCollection
import jakarta.persistence.FetchType
import jakarta.persistence.CollectionTable
import jakarta.persistence.JoinColumn
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType

@Entity
@Table(name = "item_category")
class ItemCategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(length = 500)
    var description: String? = null,

    @Column(name = "show_on_site", nullable = false)
    var showOnSite: Boolean = true,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "active", nullable = false)
    var active: Boolean = true,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_category_available_types", joinColumns = [JoinColumn(name = "category_id")])
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    var availableTypes: MutableSet<ItemType> = ItemType.entries.toMutableSet()
) : BaseEntity()
