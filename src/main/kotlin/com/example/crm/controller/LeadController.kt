package com.example.crm.controller

import com.example.crm.dto.request.LeadMessageRequest
import com.example.crm.dto.request.LeadRequest
import com.example.crm.dto.response.LeadMessageResponse
import com.example.crm.dto.response.LeadResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.entity.LeadEntity
import com.example.crm.entity.LeadMessageEntity
import com.example.crm.service.LeadService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/leads")
class LeadController(
    private val service: LeadService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<LeadResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("id").descending()), tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<LeadResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: LeadRequest): ResponseEntity<LeadResponse> {
        val created = service.create(LeadEntity(
            tenantId = request.tenantId, flowId = request.flowId, customerId = request.customerId,
            status = request.status, source = request.source,
            estimatedValueCents = request.estimatedValueCents, notes = request.notes
        ))
        return ResponseEntity.created(URI.create("/api/v1/leads/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: LeadRequest): ResponseEntity<LeadResponse> =
        ResponseEntity.ok(service.update(id, LeadEntity(
            tenantId = request.tenantId, flowId = request.flowId, customerId = request.customerId,
            status = request.status, source = request.source,
            estimatedValueCents = request.estimatedValueCents, notes = request.notes
        )).toResponse())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{leadId}/messages")
    fun listMessages(@PathVariable leadId: Long): ResponseEntity<List<LeadMessageResponse>> =
        ResponseEntity.ok(service.getMessages(leadId).map { it.toMessageResponse() })

    @PostMapping("/{leadId}/messages")
    fun createMessage(@PathVariable leadId: Long, @RequestBody request: LeadMessageRequest): ResponseEntity<LeadMessageResponse> {
        val created = service.createMessage(leadId, LeadMessageEntity(
            leadId = leadId, message = request.message, channel = request.channel,
            createdByUserId = request.createdByUserId
        ))
        return ResponseEntity.created(URI.create("/api/v1/leads/$leadId/messages/${created.id}")).body(created.toMessageResponse())
    }

    private fun LeadEntity.toResponse() = LeadResponse(id, code, tenantId, flowId, customerId, status, source, estimatedValueCents, notes, createdAt, updatedAt)
    private fun LeadMessageEntity.toMessageResponse() = LeadMessageResponse(id, leadId, message, channel, createdByUserId, createdAt)
}
