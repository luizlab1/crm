package com.example.crm.repository

import com.example.crm.entity.LeadMessageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LeadMessageRepository : JpaRepository<LeadMessageEntity, Long> {
    fun findByLeadId(leadId: Long): List<LeadMessageEntity>
}
