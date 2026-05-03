package com.example.crm.repository

import com.example.crm.entity.LeadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LeadRepository : JpaRepository<LeadEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<LeadEntity>
}
