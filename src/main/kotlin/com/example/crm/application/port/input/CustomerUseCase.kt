package com.example.crm.application.port.input

import com.example.crm.domain.model.Customer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomerUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<Customer>
    fun getById(id: Long): Customer
    fun create(customer: Customer): Customer
    fun update(id: Long, customer: Customer): Customer
    fun delete(id: Long)
}

