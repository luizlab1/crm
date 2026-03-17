package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.LeadUseCase
import com.example.crm.infrastructure.web.dto.request.LeadMessageRequest
import com.example.crm.infrastructure.web.dto.request.LeadRequest
import com.example.crm.infrastructure.web.dto.response.LeadMessageResponse
import com.example.crm.infrastructure.web.dto.response.LeadResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.mapper.LeadWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/leads")
class LeadController(
    private val useCase: LeadUseCase,
    private val mapper: LeadWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<LeadResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        val result = useCase.list(pageable, tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<LeadResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @PostMapping
    fun create(@RequestBody request: LeadRequest): ResponseEntity<LeadResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/leads/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: LeadRequest): ResponseEntity<LeadResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{leadId}/messages")
    fun findMessages(@PathVariable leadId: Long): ResponseEntity<List<LeadMessageResponse>> =
        ResponseEntity.ok(useCase.getMessages(leadId).map { mapper.toResponse(it) })

    @PostMapping("/{leadId}/messages")
    fun createMessage(
        @PathVariable leadId: Long,
        @RequestBody request: LeadMessageRequest
    ): ResponseEntity<LeadMessageResponse> {
        val created = useCase.createMessage(leadId, mapper.messageToDomain(leadId, request))
        return ResponseEntity.created(URI.create("/api/v1/leads/$leadId/messages/${created.id}"))
            .body(mapper.toResponse(created))
    }
}
