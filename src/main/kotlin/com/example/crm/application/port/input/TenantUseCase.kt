package com.example.crm.application.port.input

import com.example.crm.domain.model.Tenant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TenantUseCase {
    fun list(pageable: Pageable): Page<Tenant>
    fun getById(id: Long): Tenant
    fun create(tenant: Tenant): Tenant
    fun update(id: Long, tenant: Tenant): Tenant
    fun delete(id: Long)
}

