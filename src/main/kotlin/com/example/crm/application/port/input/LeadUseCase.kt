package com.example.crm.application.port.input

import com.example.crm.domain.model.Lead
import com.example.crm.domain.model.LeadMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LeadUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<Lead>
    fun getById(id: Long): Lead
    fun create(lead: Lead): Lead
    fun update(id: Long, lead: Lead): Lead
    fun delete(id: Long)
    fun getMessages(leadId: Long): List<LeadMessage>
    fun createMessage(leadId: Long, message: LeadMessage): LeadMessage
}

