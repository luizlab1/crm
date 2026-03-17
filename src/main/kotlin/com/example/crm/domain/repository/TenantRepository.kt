package com.example.crm.domain.repository

import com.example.crm.domain.model.Tenant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TenantRepository {
    fun findAll(pageable: Pageable): Page<Tenant>
    fun findById(id: Long): Tenant?
    fun save(tenant: Tenant): Tenant
}

