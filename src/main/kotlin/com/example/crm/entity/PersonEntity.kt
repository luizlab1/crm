package com.example.crm.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "person")
class PersonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var physical: PersonPhysicalEntity? = null,

    @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var legal: PersonLegalEntity? = null,

    @OneToMany(mappedBy = "personId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var contacts: MutableList<ContactEntity> = mutableListOf()
) : BaseEntity()
