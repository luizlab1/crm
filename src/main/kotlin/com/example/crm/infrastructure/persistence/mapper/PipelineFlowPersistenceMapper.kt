package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.PipelineFlow
import com.example.crm.domain.model.PipelineFlowStep
import com.example.crm.infrastructure.persistence.entity.PipelineFlowJpaEntity
import com.example.crm.infrastructure.persistence.entity.PipelineFlowStepJpaEntity
import org.springframework.stereotype.Component

@Component
class PipelineFlowPersistenceMapper {

    fun toDomain(entity: PipelineFlowJpaEntity): PipelineFlow = PipelineFlow(
        id = entity.id, tenantId = entity.tenantId, code = entity.code,
        name = entity.name, description = entity.description, isActive = entity.isActive,
        steps = entity.steps.map { toDomain(it) },
        createdAt = entity.createdAt, updatedAt = entity.updatedAt
    )

    fun toDomain(entity: PipelineFlowStepJpaEntity): PipelineFlowStep = PipelineFlowStep(
        id = entity.id, stepOrder = entity.stepOrder, code = entity.code,
        name = entity.name, description = entity.description,
        stepType = entity.stepType, isTerminal = entity.isTerminal,
        createdAt = entity.createdAt, updatedAt = entity.updatedAt
    )

    fun toEntity(domain: PipelineFlow): PipelineFlowJpaEntity {
        val entity = PipelineFlowJpaEntity(
            id = domain.id, tenantId = domain.tenantId, code = domain.code,
            name = domain.name, description = domain.description, isActive = domain.isActive
        )
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        domain.steps.forEach { s ->
            val step = PipelineFlowStepJpaEntity(
                id = s.id, pipelineFlowId = domain.id, stepOrder = s.stepOrder,
                code = s.code, name = s.name, description = s.description,
                stepType = s.stepType, isTerminal = s.isTerminal
            )
            step.createdAt = s.createdAt
            step.updatedAt = s.updatedAt
            entity.steps.add(step)
        }
        return entity
    }
}

