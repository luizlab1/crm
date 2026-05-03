package com.example.crm.repository

import com.example.crm.entity.WorkerEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface WorkerRepository : JpaRepository<WorkerEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<WorkerEntity>
}
