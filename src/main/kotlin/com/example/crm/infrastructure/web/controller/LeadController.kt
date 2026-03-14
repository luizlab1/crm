package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.LeadJpaEntity
import com.example.crm.infrastructure.persistence.entity.LeadMessageJpaEntity
import com.example.crm.infrastructure.persistence.repository.LeadJpaRepository
import com.example.crm.infrastructure.persistence.repository.LeadMessageJpaRepository
import com.example.crm.infrastructure.web.dto.request.LeadMessageRequest
import com.example.crm.infrastructure.web.dto.request.LeadRequest
import com.example.crm.infrastructure.web.dto.response.LeadMessageResponse
import com.example.crm.infrastructure.web.dto.response.LeadResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/leads")
class LeadController(
    private val repository: LeadJpaRepository,
    private val messageRepository: LeadMessageJpaRepository
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<LeadResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        val result = if (tenantId != null) repository.findByTenantId(tenantId, pageable)
                     else repository.findAll(pageable)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<LeadResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Lead not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: LeadRequest): ResponseEntity<LeadResponse> {
        val entity = LeadJpaEntity(
            tenantId = request.tenantId, flowId = request.flowId,
            customerId = request.customerId, status = request.status,
            source = request.source, estimatedValueCents = request.estimatedValueCents,
            notes = request.notes
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/leads/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: LeadRequest): ResponseEntity<LeadResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Lead not found: $id") }
        entity.tenantId = request.tenantId
        entity.flowId = request.flowId
        entity.customerId = request.customerId
        entity.status = request.status
        entity.source = request.source
        entity.estimatedValueCents = request.estimatedValueCents
        entity.notes = request.notes
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        repository.findById(id).orElseThrow { NoSuchElementException("Lead not found: $id") }
        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    // --- Lead Messages ---

    @GetMapping("/{leadId}/messages")
    fun findMessages(@PathVariable leadId: Long): ResponseEntity<List<LeadMessageResponse>> {
        repository.findById(leadId).orElseThrow { NoSuchElementException("Lead not found: $leadId") }
        val messages = messageRepository.findByLeadId(leadId).map { it.toResponse() }
        return ResponseEntity.ok(messages)
    }

    @PostMapping("/{leadId}/messages")
    fun createMessage(
        @PathVariable leadId: Long,
        @RequestBody request: LeadMessageRequest
    ): ResponseEntity<LeadMessageResponse> {
        repository.findById(leadId).orElseThrow { NoSuchElementException("Lead not found: $leadId") }
        val entity = LeadMessageJpaEntity(
            leadId = leadId, message = request.message,
            channel = request.channel, createdByUserId = request.createdByUserId
        )
        val saved = messageRepository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/leads/$leadId/messages/${saved.id}"))
            .body(saved.toResponse())
    }

    private fun LeadJpaEntity.toResponse() = LeadResponse(
        id = id, code = code, tenantId = tenantId, flowId = flowId,
        customerId = customerId, status = status, source = source,
        estimatedValueCents = estimatedValueCents, notes = notes,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun LeadMessageJpaEntity.toResponse() = LeadMessageResponse(
        id = id, leadId = leadId, message = message,
        channel = channel, createdByUserId = createdByUserId, createdAt = createdAt
    )
}

