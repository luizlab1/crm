package com.example.crm.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "lead")
class LeadEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, updatable = false)
    val code: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "flow_id", nullable = false)
    var flowId: Long = 0,

    @Column(name = "customer_id")
    var customerId: Long? = null,

    @Column(nullable = false, length = 30)
    var status: String = "NEW",

    @Column(length = 100)
    var source: String? = null,

    @Column(name = "estimated_value_cents")
    var estimatedValueCents: Long? = null,

    @Column(length = 1000)
    var notes: String? = null
) : BaseEntity()
