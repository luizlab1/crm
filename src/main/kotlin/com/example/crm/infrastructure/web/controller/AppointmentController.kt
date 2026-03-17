package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.AppointmentUseCase
import com.example.crm.infrastructure.web.dto.request.AppointmentRequest
import com.example.crm.infrastructure.web.dto.response.AppointmentResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.mapper.AppointmentWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/appointments")
class AppointmentController(
    private val useCase: AppointmentUseCase,
    private val mapper: AppointmentWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<AppointmentResponse>> {
        val result = useCase.list(PageRequest.of(page, size, Sort.by("scheduledAt").descending()))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<AppointmentResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @PostMapping
    fun create(@RequestBody request: AppointmentRequest): ResponseEntity<AppointmentResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/appointments/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: AppointmentRequest): ResponseEntity<AppointmentResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
