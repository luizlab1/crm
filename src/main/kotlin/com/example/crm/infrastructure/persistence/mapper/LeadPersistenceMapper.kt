package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.Lead
import com.example.crm.domain.model.LeadMessage
import com.example.crm.infrastructure.persistence.entity.LeadJpaEntity
import com.example.crm.infrastructure.persistence.entity.LeadMessageJpaEntity
import org.springframework.stereotype.Component

@Component
class LeadPersistenceMapper {

    fun toDomain(entity: LeadJpaEntity): Lead = Lead(
        id = entity.id, code = entity.code, tenantId = entity.tenantId,
        flowId = entity.flowId, customerId = entity.customerId, status = entity.status,
        source = entity.source, estimatedValueCents = entity.estimatedValueCents,
        notes = entity.notes, createdAt = entity.createdAt, updatedAt = entity.updatedAt
    )

    fun toEntity(domain: Lead): LeadJpaEntity {
        val entity = LeadJpaEntity(
            id = domain.id, code = domain.code, tenantId = domain.tenantId,
            flowId = domain.flowId, customerId = domain.customerId, status = domain.status,
            source = domain.source, estimatedValueCents = domain.estimatedValueCents,
            notes = domain.notes
        )
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        return entity
    }

    fun toDomain(entity: LeadMessageJpaEntity): LeadMessage = LeadMessage(
        id = entity.id, leadId = entity.leadId, message = entity.message,
        channel = entity.channel, createdByUserId = entity.createdByUserId,
        createdAt = entity.createdAt
    )

    fun toEntity(domain: LeadMessage): LeadMessageJpaEntity = LeadMessageJpaEntity(
        id = domain.id, leadId = domain.leadId, message = domain.message,
        channel = domain.channel, createdByUserId = domain.createdByUserId
    )
}

