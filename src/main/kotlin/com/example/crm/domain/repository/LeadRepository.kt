package com.example.crm.domain.repository

import com.example.crm.domain.model.Lead
import com.example.crm.domain.model.LeadMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LeadRepository {
    fun findAll(pageable: Pageable): Page<Lead>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Lead>
    fun findById(id: Long): Lead?
    fun save(lead: Lead): Lead
    fun deleteById(id: Long)
    fun findMessagesByLeadId(leadId: Long): List<LeadMessage>
    fun saveMessage(message: LeadMessage): LeadMessage
}

