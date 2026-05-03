package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "worker")
class WorkerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "person_id", nullable = false)
    var personId: Long = 0,

    @Column(name = "user_id")
    var userId: Long? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
