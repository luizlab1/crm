package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.RoleJpaEntity
import com.example.crm.infrastructure.persistence.repository.RoleJpaRepository
import com.example.crm.infrastructure.web.dto.request.RoleRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.RoleResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/roles")
class RoleController(private val repository: RoleJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<RoleResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("name")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<RoleResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Role not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: RoleRequest): ResponseEntity<RoleResponse> {
        val entity = RoleJpaEntity(name = request.name, description = request.description, isActive = request.isActive)
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/roles/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: RoleRequest): ResponseEntity<RoleResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Role not found: $id") }
        entity.name = request.name
        entity.description = request.description
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Role not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun RoleJpaEntity.toResponse() = RoleResponse(
        id = id, name = name, description = description, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

