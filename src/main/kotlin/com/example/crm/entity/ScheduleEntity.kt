package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "schedule")
class ScheduleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "customer_id", nullable = false)
    var customerId: Long = 0,

    @Column(name = "appointment_id", nullable = false)
    var appointmentId: Long = 0,

    @Column(length = 1000)
    var description: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
