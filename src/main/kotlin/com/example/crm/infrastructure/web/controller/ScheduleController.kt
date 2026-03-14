package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.ScheduleJpaEntity
import com.example.crm.infrastructure.persistence.repository.ScheduleJpaRepository
import com.example.crm.infrastructure.web.dto.request.ScheduleRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.ScheduleResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/schedules")
class ScheduleController(private val repository: ScheduleJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<ScheduleResponse>> {
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
    fun findById(@PathVariable id: Long): ResponseEntity<ScheduleResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Schedule not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: ScheduleRequest): ResponseEntity<ScheduleResponse> {
        val entity = ScheduleJpaEntity(
            tenantId = request.tenantId, customerId = request.customerId,
            appointmentId = request.appointmentId, description = request.description,
            isActive = request.isActive
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/schedules/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ScheduleRequest): ResponseEntity<ScheduleResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Schedule not found: $id") }
        entity.tenantId = request.tenantId
        entity.customerId = request.customerId
        entity.appointmentId = request.appointmentId
        entity.description = request.description
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Schedule not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun ScheduleJpaEntity.toResponse() = ScheduleResponse(
        id = id, code = code, tenantId = tenantId, customerId = customerId,
        appointmentId = appointmentId, description = description, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

