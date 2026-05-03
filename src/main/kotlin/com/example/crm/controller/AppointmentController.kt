package com.example.crm.controller

import com.example.crm.dto.request.AppointmentRequest
import com.example.crm.dto.response.AppointmentResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.entity.AppointmentEntity
import com.example.crm.service.AppointmentService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/appointments")
class AppointmentController(
    private val service: AppointmentService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<AppointmentResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("scheduledAt").descending()))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<AppointmentResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: AppointmentRequest): ResponseEntity<AppointmentResponse> {
        val created = service.create(AppointmentEntity(
            status = request.status, scheduledAt = request.scheduledAt,
            startedAt = request.startedAt, finishedAt = request.finishedAt,
            totalCents = request.totalCents, notes = request.notes
        ))
        return ResponseEntity.created(URI.create("/api/v1/appointments/${created.id}"))
            .body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: AppointmentRequest): ResponseEntity<AppointmentResponse> =
        ResponseEntity.ok(service.update(id, AppointmentEntity(
            status = request.status, scheduledAt = request.scheduledAt,
            startedAt = request.startedAt, finishedAt = request.finishedAt,
            totalCents = request.totalCents, notes = request.notes
        )).toResponse())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun com.example.crm.entity.AppointmentEntity.toResponse() = AppointmentResponse(
        id = id, code = code, status = status, scheduledAt = scheduledAt,
        startedAt = startedAt, finishedAt = finishedAt, totalCents = totalCents,
        notes = notes, createdAt = createdAt, updatedAt = updatedAt
    )
}
