package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.PersonJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface PersonJpaRepository : JpaRepository<PersonJpaEntity, Long> {
    // Use EntityGraph on the query methods to eagerly fetch small relations used during mapping
    @EntityGraph(attributePaths = ["physical", "legal"])
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<PersonJpaEntity>

    @EntityGraph(attributePaths = ["physical", "legal"])
    override fun findAll(pageable: Pageable): Page<PersonJpaEntity>
}

