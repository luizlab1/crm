package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.PipelineFlowUseCase
import com.example.crm.infrastructure.web.dto.request.PipelineFlowRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.PipelineFlowResponse
import com.example.crm.infrastructure.web.mapper.PipelineFlowWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/pipeline-flows")
class PipelineFlowController(
    private val useCase: PipelineFlowUseCase,
    private val mapper: PipelineFlowWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<PipelineFlowResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("name"))
        val result = useCase.list(pageable, tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PipelineFlowResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @PostMapping
    fun create(@RequestBody request: PipelineFlowRequest): ResponseEntity<PipelineFlowResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/pipeline-flows/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PipelineFlowRequest): ResponseEntity<PipelineFlowResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
