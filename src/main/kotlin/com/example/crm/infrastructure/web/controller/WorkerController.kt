package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.WorkerJpaEntity
import com.example.crm.infrastructure.persistence.repository.WorkerJpaRepository
import com.example.crm.infrastructure.web.dto.request.WorkerRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.WorkerResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/workers")
class WorkerController(private val repository: WorkerJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<WorkerResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id"))
        val result = if (tenantId != null) repository.findByTenantId(tenantId, pageable)
                     else repository.findAll(pageable)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<WorkerResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Worker not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: WorkerRequest): ResponseEntity<WorkerResponse> {
        val entity = WorkerJpaEntity(
            tenantId = request.tenantId, personId = request.personId,
            userId = request.userId, isActive = request.isActive
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/workers/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: WorkerRequest): ResponseEntity<WorkerResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Worker not found: $id") }
        entity.tenantId = request.tenantId
        entity.personId = request.personId
        entity.userId = request.userId
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Worker not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun WorkerJpaEntity.toResponse() = WorkerResponse(
        id = id, code = code, tenantId = tenantId, personId = personId,
        userId = userId, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

