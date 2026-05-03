package com.example.crm.repository

import com.example.crm.entity.PipelineFlowEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PipelineFlowRepository : JpaRepository<PipelineFlowEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<PipelineFlowEntity>
}
