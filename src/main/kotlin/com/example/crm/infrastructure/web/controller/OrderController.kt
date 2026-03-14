package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.OrderItemJpaEntity
import com.example.crm.infrastructure.persistence.entity.OrderJpaEntity
import com.example.crm.infrastructure.persistence.repository.OrderJpaRepository
import com.example.crm.infrastructure.web.dto.request.OrderRequest
import com.example.crm.infrastructure.web.dto.response.OrderItemResponse
import com.example.crm.infrastructure.web.dto.response.OrderResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val repository: OrderJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<OrderResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        val result = if (tenantId != null) repository.findByTenantId(tenantId, pageable)
                     else repository.findAll(pageable)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Order not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: OrderRequest): ResponseEntity<OrderResponse> {
        val entity = OrderJpaEntity(
            tenantId = request.tenantId, customerId = request.customerId,
            userId = request.userId, status = request.status,
            subtotalCents = request.subtotalCents, discountCents = request.discountCents,
            totalCents = request.totalCents, currencyCode = request.currencyCode,
            notes = request.notes
        )
        request.items.forEach { i ->
            entity.items.add(OrderItemJpaEntity(
                orderId = 0, tenantId = request.tenantId, itemId = i.itemId,
                quantity = i.quantity, unitPriceCents = i.unitPriceCents,
                totalPriceCents = i.totalPriceCents
            ))
        }
        val saved = repository.save(entity)
        saved.items.forEach { it.orderId = saved.id }
        val final = repository.save(saved)
        return ResponseEntity.created(URI.create("/api/v1/orders/${final.id}")).body(final.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: OrderRequest): ResponseEntity<OrderResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Order not found: $id") }
        entity.tenantId = request.tenantId
        entity.customerId = request.customerId
        entity.userId = request.userId
        entity.status = request.status
        entity.subtotalCents = request.subtotalCents
        entity.discountCents = request.discountCents
        entity.totalCents = request.totalCents
        entity.currencyCode = request.currencyCode
        entity.notes = request.notes

        entity.items.clear()
        request.items.forEach { i ->
            entity.items.add(OrderItemJpaEntity(
                orderId = entity.id, tenantId = request.tenantId, itemId = i.itemId,
                quantity = i.quantity, unitPriceCents = i.unitPriceCents,
                totalPriceCents = i.totalPriceCents
            ))
        }
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        repository.findById(id).orElseThrow { NoSuchElementException("Order not found: $id") }
        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    private fun OrderJpaEntity.toResponse() = OrderResponse(
        id = id, code = code, tenantId = tenantId, customerId = customerId,
        userId = userId, status = status, subtotalCents = subtotalCents,
        discountCents = discountCents, totalCents = totalCents,
        currencyCode = currencyCode, notes = notes,
        items = items.map { it.toResponse() },
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun OrderItemJpaEntity.toResponse() = OrderItemResponse(
        id = id, itemId = itemId, quantity = quantity,
        unitPriceCents = unitPriceCents, totalPriceCents = totalPriceCents,
        createdAt = createdAt
    )
}

