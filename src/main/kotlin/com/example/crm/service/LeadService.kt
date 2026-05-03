package com.example.crm.service

import com.example.crm.entity.LeadEntity
import com.example.crm.entity.LeadMessageEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.LeadMessageRepository
import com.example.crm.repository.LeadRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LeadService(
    private val leadRepository: LeadRepository,
    private val messageRepository: LeadMessageRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<LeadEntity> =
        if (tenantId != null) leadRepository.findByTenantId(tenantId, pageable)
        else leadRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): LeadEntity =
        leadRepository.findById(id).orElseThrow { EntityNotFoundException("Lead", id) }

    fun create(entity: LeadEntity): LeadEntity = leadRepository.save(entity)

    fun update(id: Long, entity: LeadEntity): LeadEntity {
        val existing = getById(id)
        existing.tenantId = entity.tenantId
        existing.flowId = entity.flowId
        existing.customerId = entity.customerId
        existing.status = entity.status
        existing.source = entity.source
        existing.estimatedValueCents = entity.estimatedValueCents
        existing.notes = entity.notes
        return leadRepository.save(existing)
    }

    fun delete(id: Long) {
        getById(id)
        leadRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getMessages(leadId: Long): List<LeadMessageEntity> {
        getById(leadId)
        return messageRepository.findByLeadId(leadId)
    }

    fun createMessage(leadId: Long, message: LeadMessageEntity): LeadMessageEntity {
        getById(leadId)
        message.leadId = leadId
        return messageRepository.save(message)
    }
}
