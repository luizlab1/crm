package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ItemCategoryJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ItemCategoryJpaRepository : JpaRepository<ItemCategoryJpaEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ItemCategoryJpaEntity>
}

