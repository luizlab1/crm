package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@MappedSuperclass
abstract class BaseJpaEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
}

