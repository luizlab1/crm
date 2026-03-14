package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.LeadMessageJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LeadMessageJpaRepository : JpaRepository<LeadMessageJpaEntity, Long> {
    fun findByLeadId(leadId: Long): List<LeadMessageJpaEntity>
}

