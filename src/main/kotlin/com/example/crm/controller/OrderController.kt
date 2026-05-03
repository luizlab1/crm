package com.example.crm.controller

import com.example.crm.dto.request.OrderRequest
import com.example.crm.dto.response.OrderItemResponse
import com.example.crm.dto.response.OrderResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.entity.OrderEntity
import com.example.crm.entity.OrderItemEntity
import com.example.crm.service.OrderService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val service: OrderService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<OrderResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("id").descending()), tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: OrderRequest): ResponseEntity<OrderResponse> {
        val entity = request.toEntity()
        val created = service.create(entity)
        return ResponseEntity.created(URI.create("/api/v1/orders/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: OrderRequest): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(service.update(id, request.toEntity()).toResponse())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun OrderRequest.toEntity() = OrderEntity(
        tenantId = tenantId, customerId = customerId, userId = userId, status = status,
        subtotalCents = subtotalCents, discountCents = discountCents, totalCents = totalCents,
        currencyCode = currencyCode, notes = notes
    ).also { order ->
        order.items.addAll(items.map {
            OrderItemEntity(orderId = 0L, tenantId = tenantId, itemId = it.itemId, quantity = it.quantity,
                unitPriceCents = it.unitPriceCents, totalPriceCents = it.totalPriceCents)
        })
    }

    private fun OrderEntity.toResponse() = OrderResponse(
        id = id, code = code, tenantId = tenantId, customerId = customerId, userId = userId,
        status = status, subtotalCents = subtotalCents, discountCents = discountCents,
        totalCents = totalCents, currencyCode = currencyCode, notes = notes,
        items = items.map { OrderItemResponse(it.id, it.itemId, it.quantity, it.unitPriceCents, it.totalPriceCents, it.createdAt) },
        createdAt = createdAt, updatedAt = updatedAt
    )
}
