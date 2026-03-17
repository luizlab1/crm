package com.example.crm.application.usecase

import com.example.crm.application.port.input.PipelineFlowUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.PipelineFlow
import com.example.crm.domain.repository.PipelineFlowRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PipelineFlowUseCaseImpl(
    private val pipelineFlowRepository: PipelineFlowRepository
) : PipelineFlowUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<PipelineFlow> =
        if (tenantId != null) pipelineFlowRepository.findByTenantId(tenantId, pageable)
        else pipelineFlowRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): PipelineFlow =
        pipelineFlowRepository.findById(id) ?: throw EntityNotFoundException("PipelineFlow", id)

    override fun create(pipelineFlow: PipelineFlow): PipelineFlow =
        pipelineFlowRepository.save(pipelineFlow)

    override fun update(id: Long, pipelineFlow: PipelineFlow): PipelineFlow {
        val existing = pipelineFlowRepository.findById(id) ?: throw EntityNotFoundException("PipelineFlow", id)
        val updated = pipelineFlow.copy(id = existing.id, createdAt = existing.createdAt)
        return pipelineFlowRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = pipelineFlowRepository.findById(id) ?: throw EntityNotFoundException("PipelineFlow", id)
        pipelineFlowRepository.save(existing.copy(isActive = false))
    }
}

