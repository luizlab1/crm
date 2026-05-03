package com.example.crm.entity

import jakarta.persistence.*

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
