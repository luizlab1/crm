package com.example.crm.application.port.input

import com.example.crm.domain.model.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<Order>
    fun getById(id: Long): Order
    fun create(order: Order): Order
    fun update(id: Long, order: Order): Order
    fun delete(id: Long)
}

