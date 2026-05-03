package com.example.crm.controller

import com.example.crm.dto.request.ScheduleRequest
import com.example.crm.dto.response.PageResponse
import com.example.crm.dto.response.ScheduleResponse
import com.example.crm.entity.ScheduleEntity
import com.example.crm.service.ScheduleService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/schedules")
class ScheduleController(
    private val service: ScheduleService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<ScheduleResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("id").descending()), tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ScheduleResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: ScheduleRequest): ResponseEntity<ScheduleResponse> {
        val created = service.create(
            ScheduleEntity(
                tenantId = request.tenantId,
                customerId = request.customerId,
                appointmentId = request.appointmentId,
                description = request.description,
                isActive = request.isActive
            )
        )
        val uri = URI.create("/api/v1/schedules/${created.id}")
        return ResponseEntity.created(uri).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ScheduleRequest): ResponseEntity<ScheduleResponse> =
        ResponseEntity.ok(
            service.update(
                id,
                ScheduleEntity(
                    tenantId = request.tenantId,
                    customerId = request.customerId,
                    appointmentId = request.appointmentId,
                    description = request.description,
                    isActive = request.isActive
                )
            ).toResponse()
        )

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun ScheduleEntity.toResponse() = ScheduleResponse(
        id = id,
        code = code,
        tenantId = tenantId,
        customerId = customerId,
        appointmentId = appointmentId,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
