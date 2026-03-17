package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.Order
import com.example.crm.domain.model.OrderItem
import com.example.crm.infrastructure.persistence.entity.OrderItemJpaEntity
import com.example.crm.infrastructure.persistence.entity.OrderJpaEntity
import org.springframework.stereotype.Component

@Component
class OrderPersistenceMapper {

    fun toDomain(entity: OrderJpaEntity): Order = Order(
        id = entity.id, code = entity.code, tenantId = entity.tenantId,
        customerId = entity.customerId, userId = entity.userId, status = entity.status,
        subtotalCents = entity.subtotalCents, discountCents = entity.discountCents,
        totalCents = entity.totalCents, currencyCode = entity.currencyCode, notes = entity.notes,
        items = entity.items.map { toDomain(it) },
        createdAt = entity.createdAt, updatedAt = entity.updatedAt
    )

    fun toDomain(entity: OrderItemJpaEntity): OrderItem = OrderItem(
        id = entity.id, itemId = entity.itemId, quantity = entity.quantity,
        unitPriceCents = entity.unitPriceCents, totalPriceCents = entity.totalPriceCents,
        createdAt = entity.createdAt
    )

    fun toEntity(domain: Order): OrderJpaEntity {
        val entity = OrderJpaEntity(
            id = domain.id, code = domain.code, tenantId = domain.tenantId,
            customerId = domain.customerId, userId = domain.userId, status = domain.status,
            subtotalCents = domain.subtotalCents, discountCents = domain.discountCents,
            totalCents = domain.totalCents, currencyCode = domain.currencyCode, notes = domain.notes
        )
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        domain.items.forEach { i ->
            entity.items.add(OrderItemJpaEntity(
                id = i.id, orderId = domain.id, tenantId = domain.tenantId,
                itemId = i.itemId, quantity = i.quantity,
                unitPriceCents = i.unitPriceCents, totalPriceCents = i.totalPriceCents
            ))
        }
        return entity
    }
}

