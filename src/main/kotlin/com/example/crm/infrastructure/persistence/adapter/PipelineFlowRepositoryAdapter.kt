package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.PipelineFlow
import com.example.crm.domain.repository.PipelineFlowRepository
import com.example.crm.infrastructure.persistence.mapper.PipelineFlowPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.PipelineFlowJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PipelineFlowRepositoryAdapter(
    private val jpaRepository: PipelineFlowJpaRepository,
    private val mapper: PipelineFlowPersistenceMapper
) : PipelineFlowRepository {

    override fun findAll(pageable: Pageable): Page<PipelineFlow> =
        jpaRepository.findAll(pageable).map { mapper.toDomain(it) }

    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<PipelineFlow> =
        jpaRepository.findByTenantId(tenantId, pageable).map { mapper.toDomain(it) }

    override fun findById(id: Long): PipelineFlow? =
        jpaRepository.findById(id).map { mapper.toDomain(it) }.orElse(null)

    override fun save(pipelineFlow: PipelineFlow): PipelineFlow {
        val entity = mapper.toEntity(pipelineFlow)
        val saved = jpaRepository.save(entity)
        if (pipelineFlow.id == 0L) {
            saved.steps.forEach { it.pipelineFlowId = saved.id }
            return mapper.toDomain(jpaRepository.save(saved))
        }
        return mapper.toDomain(saved)
    }
}

