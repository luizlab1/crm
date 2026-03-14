package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.PipelineFlowJpaEntity
import com.example.crm.infrastructure.persistence.entity.PipelineFlowStepJpaEntity
import com.example.crm.infrastructure.persistence.repository.PipelineFlowJpaRepository
import com.example.crm.infrastructure.web.dto.request.PipelineFlowRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.PipelineFlowResponse
import com.example.crm.infrastructure.web.dto.response.PipelineFlowStepResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/pipeline-flows")
class PipelineFlowController(private val repository: PipelineFlowJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<PipelineFlowResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("name"))
        val result = if (tenantId != null) repository.findByTenantId(tenantId, pageable)
                     else repository.findAll(pageable)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PipelineFlowResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("PipelineFlow not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: PipelineFlowRequest): ResponseEntity<PipelineFlowResponse> {
        val entity = PipelineFlowJpaEntity(
            tenantId = request.tenantId, code = request.code,
            name = request.name, description = request.description, isActive = request.isActive
        )
        request.steps.forEach { s ->
            entity.steps.add(PipelineFlowStepJpaEntity(
                pipelineFlowId = 0, stepOrder = s.stepOrder, code = s.code,
                name = s.name, description = s.description,
                stepType = s.stepType, isTerminal = s.isTerminal
            ))
        }
        val saved = repository.save(entity)
        saved.steps.forEach { it.pipelineFlowId = saved.id }
        val final = repository.save(saved)
        return ResponseEntity.created(URI.create("/api/v1/pipeline-flows/${final.id}")).body(final.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PipelineFlowRequest): ResponseEntity<PipelineFlowResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("PipelineFlow not found: $id") }
        entity.tenantId = request.tenantId
        entity.code = request.code
        entity.name = request.name
        entity.description = request.description
        entity.isActive = request.isActive

        entity.steps.clear()
        request.steps.forEach { s ->
            entity.steps.add(PipelineFlowStepJpaEntity(
                pipelineFlowId = entity.id, stepOrder = s.stepOrder, code = s.code,
                name = s.name, description = s.description,
                stepType = s.stepType, isTerminal = s.isTerminal
            ))
        }
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("PipelineFlow not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun PipelineFlowJpaEntity.toResponse() = PipelineFlowResponse(
        id = id, tenantId = tenantId, code = code, name = name,
        description = description, isActive = isActive,
        steps = steps.map { it.toResponse() },
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun PipelineFlowStepJpaEntity.toResponse() = PipelineFlowStepResponse(
        id = id, stepOrder = stepOrder, code = code, name = name,
        description = description, stepType = stepType, isTerminal = isTerminal,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

