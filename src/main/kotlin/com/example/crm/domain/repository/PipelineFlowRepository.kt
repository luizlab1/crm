package com.example.crm.domain.repository

import com.example.crm.domain.model.PipelineFlow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PipelineFlowRepository {
    fun findAll(pageable: Pageable): Page<PipelineFlow>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<PipelineFlow>
    fun findById(id: Long): PipelineFlow?
    fun save(pipelineFlow: PipelineFlow): PipelineFlow
}

