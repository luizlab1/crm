package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.CustomerJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerJpaRepository : JpaRepository<CustomerJpaEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<CustomerJpaEntity>
}

