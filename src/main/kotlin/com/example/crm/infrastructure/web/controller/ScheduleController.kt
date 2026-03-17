package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.ScheduleUseCase
import com.example.crm.infrastructure.web.dto.request.ScheduleRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.ScheduleResponse
import com.example.crm.infrastructure.web.mapper.ScheduleWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/schedules")
class ScheduleController(
    private val useCase: ScheduleUseCase,
    private val mapper: ScheduleWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<ScheduleResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        val result = useCase.list(pageable, tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ScheduleResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @PostMapping
    fun create(@RequestBody request: ScheduleRequest): ResponseEntity<ScheduleResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/schedules/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ScheduleRequest): ResponseEntity<ScheduleResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
