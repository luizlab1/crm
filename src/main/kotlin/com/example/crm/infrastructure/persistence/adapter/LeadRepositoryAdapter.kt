package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.Lead
import com.example.crm.domain.model.LeadMessage
import com.example.crm.domain.repository.LeadRepository
import com.example.crm.infrastructure.persistence.mapper.LeadPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.LeadJpaRepository
import com.example.crm.infrastructure.persistence.repository.LeadMessageJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class LeadRepositoryAdapter(
    private val jpaRepository: LeadJpaRepository,
    private val messageJpaRepository: LeadMessageJpaRepository,
    private val mapper: LeadPersistenceMapper
) : LeadRepository {

    override fun findAll(pageable: Pageable): Page<Lead> =
        jpaRepository.findAll(pageable).map { mapper.toDomain(it) }

    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Lead> =
        jpaRepository.findByTenantId(tenantId, pageable).map { mapper.toDomain(it) }

    override fun findById(id: Long): Lead? =
        jpaRepository.findById(id).map { mapper.toDomain(it) }.orElse(null)

    override fun save(lead: Lead): Lead =
        mapper.toDomain(jpaRepository.save(mapper.toEntity(lead)))

    override fun deleteById(id: Long) = jpaRepository.deleteById(id)

    override fun findMessagesByLeadId(leadId: Long): List<LeadMessage> =
        messageJpaRepository.findByLeadId(leadId).map { mapper.toDomain(it) }

    override fun saveMessage(message: LeadMessage): LeadMessage =
        mapper.toDomain(messageJpaRepository.save(mapper.toEntity(message)))
}

