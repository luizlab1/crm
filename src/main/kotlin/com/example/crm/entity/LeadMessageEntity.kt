package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "lead_message")
class LeadMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "lead_id", nullable = false)
    var leadId: Long = 0,

    @Column(nullable = false, length = 2000)
    var message: String = "",

    @Column(length = 50)
    var channel: String? = null,

    @Column(name = "created_by_user_id")
    var createdByUserId: Long? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()
)
