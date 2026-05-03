package com.example.crm.repository

import com.example.crm.entity.PersonEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository : JpaRepository<PersonEntity, Long> {
    @EntityGraph(attributePaths = ["physical", "legal"])
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<PersonEntity>

    @EntityGraph(attributePaths = ["physical", "legal"])
    override fun findAll(pageable: Pageable): Page<PersonEntity>
}
