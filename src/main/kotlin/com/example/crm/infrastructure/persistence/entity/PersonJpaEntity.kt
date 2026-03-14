package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "person")
class PersonJpaEntity(
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
    var physical: PersonPhysicalJpaEntity? = null,

    @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var legal: PersonLegalJpaEntity? = null,

    @OneToMany(mappedBy = "personId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var contacts: MutableList<ContactJpaEntity> = mutableListOf()
) : BaseJpaEntity()

