package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.PermissionJpaEntity
import com.example.crm.infrastructure.persistence.repository.PermissionJpaRepository
import com.example.crm.infrastructure.web.dto.request.PermissionRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.PermissionResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/permissions")
class PermissionController(private val repository: PermissionJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<PermissionResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("code")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PermissionResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Permission not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: PermissionRequest): ResponseEntity<PermissionResponse> {
        val entity = PermissionJpaEntity(code = request.code, description = request.description, isActive = request.isActive)
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/permissions/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PermissionRequest): ResponseEntity<PermissionResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Permission not found: $id") }
        entity.code = request.code
        entity.description = request.description
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Permission not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun PermissionJpaEntity.toResponse() = PermissionResponse(
        id = id, code = code, description = description, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

