package com.example.crm.application.usecase

import com.example.crm.application.port.input.LeadUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Lead
import com.example.crm.domain.model.LeadMessage
import com.example.crm.domain.repository.LeadRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LeadUseCaseImpl(
    private val leadRepository: LeadRepository
) : LeadUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Lead> =
        if (tenantId != null) leadRepository.findByTenantId(tenantId, pageable)
        else leadRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Lead =
        leadRepository.findById(id) ?: throw EntityNotFoundException("Lead", id)

    override fun create(lead: Lead): Lead =
        leadRepository.save(lead)

    override fun update(id: Long, lead: Lead): Lead {
        val existing = leadRepository.findById(id) ?: throw EntityNotFoundException("Lead", id)
        val updated = lead.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return leadRepository.save(updated)
    }

    override fun delete(id: Long) {
        leadRepository.findById(id) ?: throw EntityNotFoundException("Lead", id)
        leadRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun getMessages(leadId: Long): List<LeadMessage> {
        leadRepository.findById(leadId) ?: throw EntityNotFoundException("Lead", leadId)
        return leadRepository.findMessagesByLeadId(leadId)
    }

    override fun createMessage(leadId: Long, message: LeadMessage): LeadMessage {
        leadRepository.findById(leadId) ?: throw EntityNotFoundException("Lead", leadId)
        return leadRepository.saveMessage(message.copy(leadId = leadId))
    }
}

