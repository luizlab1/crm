package com.example.crm.service

import com.example.crm.entity.PipelineFlowEntity
import com.example.crm.entity.PipelineFlowStepEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.PipelineFlowRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PipelineFlowService(
    private val repository: PipelineFlowRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<PipelineFlowEntity> =
        if (tenantId != null) repository.findByTenantId(tenantId, pageable)
        else repository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): PipelineFlowEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("PipelineFlow", id) }

    fun create(entity: PipelineFlowEntity): PipelineFlowEntity {
        val saved = repository.save(entity)
        saved.steps.forEach { it.pipelineFlowId = saved.id }
        return repository.save(saved)
    }

    fun update(id: Long, entity: PipelineFlowEntity): PipelineFlowEntity {
        val existing = getById(id)
        existing.tenantId = entity.tenantId
        existing.code = entity.code
        existing.name = entity.name
        existing.description = entity.description
        existing.isActive = entity.isActive
        existing.steps.clear()
        entity.steps.forEach { step ->
            existing.steps.add(PipelineFlowStepEntity(
                pipelineFlowId = existing.id,
                stepOrder = step.stepOrder,
                code = step.code,
                name = step.name,
                description = step.description,
                stepType = step.stepType,
                isTerminal = step.isTerminal
            ))
        }
        return repository.save(existing)
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        repository.save(existing)
    }
}
