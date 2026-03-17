package com.example.crm.domain.repository

import com.example.crm.domain.model.Worker
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WorkerRepository {
    fun findAll(pageable: Pageable): Page<Worker>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Worker>
    fun findById(id: Long): Worker?
    fun save(worker: Worker): Worker
}

