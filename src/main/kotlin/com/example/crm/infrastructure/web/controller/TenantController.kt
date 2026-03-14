package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.TenantJpaEntity
import com.example.crm.infrastructure.persistence.repository.TenantJpaRepository
import com.example.crm.infrastructure.web.dto.request.TenantRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.TenantResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/tenants")
class TenantController(private val repository: TenantJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<TenantResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("name")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<TenantResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Tenant not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: TenantRequest): ResponseEntity<TenantResponse> {
        val entity = TenantJpaEntity(
            parentTenantId = request.parentTenantId,
            name = request.name,
            category = request.category,
            isActive = request.isActive
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/tenants/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: TenantRequest): ResponseEntity<TenantResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Tenant not found: $id") }
        entity.parentTenantId = request.parentTenantId
        entity.name = request.name
        entity.category = request.category
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Tenant not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun TenantJpaEntity.toResponse() = TenantResponse(
        id = id, parentTenantId = parentTenantId, code = code,
        name = name, category = category, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

