package com.example.crm.domain.repository

import com.example.crm.domain.model.Customer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomerRepository {
    fun findAll(pageable: Pageable): Page<Customer>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Customer>
    fun findById(id: Long): Customer?
    fun save(customer: Customer): Customer
}

