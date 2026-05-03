package com.example.crm.repository

import com.example.crm.entity.OrderEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderRepository : JpaRepository<OrderEntity, Long> {
    // Ensure items collection is fetched within the repository call to avoid lazy-init outside session
    @EntityGraph(attributePaths = ["items"])
    override fun findAll(pageable: Pageable): Page<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    override fun findById(id: Long): Optional<OrderEntity>
}
