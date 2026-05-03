package com.example.crm.controller

import com.example.crm.dto.request.PipelineFlowRequest
import com.example.crm.dto.response.PageResponse
import com.example.crm.dto.response.PipelineFlowResponse
import com.example.crm.dto.response.PipelineFlowStepResponse
import com.example.crm.entity.PipelineFlowEntity
import com.example.crm.entity.PipelineFlowStepEntity
import com.example.crm.service.PipelineFlowService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/pipeline-flows")
class PipelineFlowController(
    private val service: PipelineFlowService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<PipelineFlowResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("name")), tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PipelineFlowResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: PipelineFlowRequest): ResponseEntity<PipelineFlowResponse> {
        val entity = request.toEntity()
        val created = service.create(entity)
        return ResponseEntity.created(URI.create("/api/v1/pipeline-flows/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PipelineFlowRequest): ResponseEntity<PipelineFlowResponse> =
        ResponseEntity.ok(service.update(id, request.toEntity()).toResponse())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun PipelineFlowRequest.toEntity() = PipelineFlowEntity(
        tenantId = tenantId, code = code, name = name, description = description, isActive = isActive
    ).also { flow ->
        flow.steps.addAll(steps.map {
            PipelineFlowStepEntity(
                pipelineFlowId = 0L, stepOrder = it.stepOrder, code = it.code, name = it.name,
                description = it.description, stepType = it.stepType, isTerminal = it.isTerminal
            )
        })
    }

    private fun PipelineFlowEntity.toResponse() = PipelineFlowResponse(
        id = id, tenantId = tenantId, code = code, name = name, description = description,
        isActive = isActive,
        steps = steps.map { PipelineFlowStepResponse(it.id, it.stepOrder, it.code, it.name, it.description, it.stepType, it.isTerminal, it.createdAt, it.updatedAt) },
        createdAt = createdAt, updatedAt = updatedAt
    )
}
