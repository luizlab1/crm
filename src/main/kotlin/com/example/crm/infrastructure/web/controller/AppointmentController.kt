package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.AppointmentJpaEntity
import com.example.crm.infrastructure.persistence.repository.AppointmentJpaRepository
import com.example.crm.infrastructure.web.dto.request.AppointmentRequest
import com.example.crm.infrastructure.web.dto.response.AppointmentResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/appointments")
class AppointmentController(private val repository: AppointmentJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<AppointmentResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("scheduledAt").descending()))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<AppointmentResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Appointment not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: AppointmentRequest): ResponseEntity<AppointmentResponse> {
        val entity = AppointmentJpaEntity(
            status = request.status, scheduledAt = request.scheduledAt,
            startedAt = request.startedAt, finishedAt = request.finishedAt,
            totalCents = request.totalCents, notes = request.notes
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/appointments/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: AppointmentRequest): ResponseEntity<AppointmentResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Appointment not found: $id") }
        entity.status = request.status
        entity.scheduledAt = request.scheduledAt
        entity.startedAt = request.startedAt
        entity.finishedAt = request.finishedAt
        entity.totalCents = request.totalCents
        entity.notes = request.notes
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        repository.findById(id).orElseThrow { NoSuchElementException("Appointment not found: $id") }
        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    private fun AppointmentJpaEntity.toResponse() = AppointmentResponse(
        id = id, code = code, status = status, scheduledAt = scheduledAt,
        startedAt = startedAt, finishedAt = finishedAt,
        totalCents = totalCents, notes = notes,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

