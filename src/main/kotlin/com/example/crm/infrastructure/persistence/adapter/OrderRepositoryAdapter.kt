package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.Order
import com.example.crm.domain.repository.OrderRepository
import com.example.crm.infrastructure.persistence.mapper.OrderPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.OrderJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class OrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository,
    private val mapper: OrderPersistenceMapper
) : OrderRepository {

    override fun findAll(pageable: Pageable): Page<Order> =
        jpaRepository.findAll(pageable).map { mapper.toDomain(it) }

    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Order> =
        jpaRepository.findByTenantId(tenantId, pageable).map { mapper.toDomain(it) }

    override fun findById(id: Long): Order? =
        jpaRepository.findById(id).map { mapper.toDomain(it) }.orElse(null)

    override fun save(order: Order): Order {
        val entity = mapper.toEntity(order)
        val saved = jpaRepository.save(entity)
        if (order.id == 0L) {
            saved.items.forEach { it.orderId = saved.id }
            return mapper.toDomain(jpaRepository.save(saved))
        }
        return mapper.toDomain(saved)
    }

    override fun deleteById(id: Long) = jpaRepository.deleteById(id)
}

