package com.example.crm.application.usecase

import com.example.crm.application.port.input.OrderUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Order
import com.example.crm.domain.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderUseCaseImpl(
    private val orderRepository: OrderRepository
) : OrderUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Order> =
        if (tenantId != null) orderRepository.findByTenantId(tenantId, pageable)
        else orderRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Order =
        orderRepository.findById(id) ?: throw EntityNotFoundException("Order", id)

    override fun create(order: Order): Order =
        orderRepository.save(order)

    override fun update(id: Long, order: Order): Order {
        val existing = orderRepository.findById(id) ?: throw EntityNotFoundException("Order", id)
        val updated = order.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return orderRepository.save(updated)
    }

    override fun delete(id: Long) {
        orderRepository.findById(id) ?: throw EntityNotFoundException("Order", id)
        orderRepository.deleteById(id)
    }
}

