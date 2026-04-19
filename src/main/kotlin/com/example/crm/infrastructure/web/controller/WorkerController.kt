package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.WorkerUseCase
import com.example.crm.infrastructure.web.dto.request.WorkerRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.WorkerResponse
import com.example.crm.infrastructure.web.dto.response.WorkerSummaryResponse
import com.example.crm.infrastructure.web.mapper.EntityPhotoResolver
import com.example.crm.infrastructure.web.mapper.WorkerWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/workers")
class WorkerController(
    private val useCase: WorkerUseCase,
    private val mapper: WorkerWebMapper,
    private val photoResolver: EntityPhotoResolver
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<WorkerSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id"))
        val result = useCase.list(pageable, tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toSummary(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<WorkerResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @PostMapping
    fun create(@RequestBody request: WorkerRequest): ResponseEntity<WorkerResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/workers/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: WorkerRequest): ResponseEntity<WorkerResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
