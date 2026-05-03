package com.example.crm.repository

import com.example.crm.entity.OrderEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<OrderEntity>
}
