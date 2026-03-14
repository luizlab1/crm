package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.ItemCategoryJpaEntity
import com.example.crm.infrastructure.persistence.repository.ItemCategoryJpaRepository
import com.example.crm.infrastructure.web.dto.request.ItemCategoryRequest
import com.example.crm.infrastructure.web.dto.response.ItemCategoryResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/item-categories")
class ItemCategoryController(private val repository: ItemCategoryJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<ItemCategoryResponse>> {
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
    fun findById(@PathVariable id: Long): ResponseEntity<ItemCategoryResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("ItemCategory not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: ItemCategoryRequest): ResponseEntity<ItemCategoryResponse> {
        val entity = ItemCategoryJpaEntity(tenantId = request.tenantId, name = request.name)
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/item-categories/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ItemCategoryRequest): ResponseEntity<ItemCategoryResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("ItemCategory not found: $id") }
        entity.tenantId = request.tenantId
        entity.name = request.name
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        repository.findById(id).orElseThrow { NoSuchElementException("ItemCategory not found: $id") }
        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    private fun ItemCategoryJpaEntity.toResponse() = ItemCategoryResponse(
        id = id, tenantId = tenantId, name = name,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

