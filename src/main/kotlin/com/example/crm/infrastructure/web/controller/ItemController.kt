package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.ItemJpaEntity
import com.example.crm.infrastructure.persistence.repository.ItemJpaRepository
import com.example.crm.infrastructure.web.dto.request.ItemRequest
import com.example.crm.infrastructure.web.dto.response.ItemResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/items")
class ItemController(private val repository: ItemJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<ItemResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("name"))
        val result = if (tenantId != null) repository.findByTenantId(tenantId, pageable)
                     else repository.findAll(pageable)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ItemResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Item not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: ItemRequest): ResponseEntity<ItemResponse> {
        val entity = ItemJpaEntity(
            tenantId = request.tenantId, categoryId = request.categoryId,
            type = request.type, name = request.name,
            sku = request.sku, isActive = request.isActive
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/items/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ItemRequest): ResponseEntity<ItemResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Item not found: $id") }
        entity.tenantId = request.tenantId
        entity.categoryId = request.categoryId
        entity.type = request.type
        entity.name = request.name
        entity.sku = request.sku
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Item not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun ItemJpaEntity.toResponse() = ItemResponse(
        id = id, code = code, tenantId = tenantId, categoryId = categoryId,
        type = type, name = name, sku = sku, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

