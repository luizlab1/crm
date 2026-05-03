package com.example.crm.repository

import com.example.crm.entity.CustomerEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<CustomerEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<CustomerEntity>
}
