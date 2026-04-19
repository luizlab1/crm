package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "item_tag")
class ItemTagJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "item_id", nullable = false)
    var itemId: Long = 0,

    @Column(nullable = false, length = 100)
    var tag: String = ""
) : BaseJpaEntity() {
    override var createdAt = super.createdAt
    override var updatedAt = super.updatedAt
}
