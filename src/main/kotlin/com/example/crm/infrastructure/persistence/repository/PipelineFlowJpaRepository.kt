package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.PipelineFlowJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PipelineFlowJpaRepository : JpaRepository<PipelineFlowJpaEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<PipelineFlowJpaEntity>
}

