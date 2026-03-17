package com.example.crm.domain.repository

import com.example.crm.domain.model.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderRepository {
    fun findAll(pageable: Pageable): Page<Order>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Order>
    fun findById(id: Long): Order?
    fun save(order: Order): Order
    fun deleteById(id: Long)
}

