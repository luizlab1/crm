package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.CustomerJpaEntity
import com.example.crm.infrastructure.persistence.repository.CustomerJpaRepository
import com.example.crm.infrastructure.web.dto.request.CustomerRequest
import com.example.crm.infrastructure.web.dto.response.CustomerResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController(private val repository: CustomerJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<CustomerResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("fullName"))
        val result = if (tenantId != null) repository.findByTenantId(tenantId, pageable)
                     else repository.findAll(pageable)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Customer not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse> {
        val entity = CustomerJpaEntity(
            tenantId = request.tenantId, personId = request.personId,
            fullName = request.fullName, email = request.email,
            phone = request.phone, document = request.document, isActive = request.isActive
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/customers/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Customer not found: $id") }
        entity.tenantId = request.tenantId
        entity.personId = request.personId
        entity.fullName = request.fullName
        entity.email = request.email
        entity.phone = request.phone
        entity.document = request.document
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Customer not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun CustomerJpaEntity.toResponse() = CustomerResponse(
        id = id, code = code, tenantId = tenantId, personId = personId,
        fullName = fullName, email = email, phone = phone, document = document,
        isActive = isActive, createdAt = createdAt, updatedAt = updatedAt
    )
}

