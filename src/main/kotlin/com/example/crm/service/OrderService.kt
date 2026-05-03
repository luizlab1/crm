package com.example.crm.service

import com.example.crm.entity.OrderEntity
import com.example.crm.entity.OrderItemEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<OrderEntity> =
        if (tenantId != null) orderRepository.findByTenantId(tenantId, pageable)
        else orderRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): OrderEntity =
        orderRepository.findById(id).orElseThrow { EntityNotFoundException("Order", id) }

    fun create(entity: OrderEntity): OrderEntity {
        val saved = orderRepository.save(entity)
        // fix FK after first insert
        saved.items.forEach { it.orderId = saved.id }
        return orderRepository.save(saved)
    }

    fun update(id: Long, entity: OrderEntity): OrderEntity {
        val existing = getById(id)
        existing.tenantId = entity.tenantId
        existing.customerId = entity.customerId
        existing.userId = entity.userId
        existing.status = entity.status
        existing.subtotalCents = entity.subtotalCents
        existing.discountCents = entity.discountCents
        existing.totalCents = entity.totalCents
        existing.currencyCode = entity.currencyCode
        existing.notes = entity.notes
        existing.items.clear()
        entity.items.forEach { item ->
            existing.items.add(OrderItemEntity(
                orderId = existing.id,
                tenantId = item.tenantId,
                itemId = item.itemId,
                quantity = item.quantity,
                unitPriceCents = item.unitPriceCents,
                totalPriceCents = item.totalPriceCents
            ))
        }
        return orderRepository.save(existing)
    }

    fun delete(id: Long) {
        getById(id)
        orderRepository.deleteById(id)
    }
}
