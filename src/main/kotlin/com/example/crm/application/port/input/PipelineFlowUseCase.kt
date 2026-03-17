package com.example.crm.application.port.input

import com.example.crm.domain.model.PipelineFlow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PipelineFlowUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<PipelineFlow>
    fun getById(id: Long): PipelineFlow
    fun create(pipelineFlow: PipelineFlow): PipelineFlow
    fun update(id: Long, pipelineFlow: PipelineFlow): PipelineFlow
    fun delete(id: Long)
}

