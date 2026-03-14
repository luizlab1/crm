package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.UnitOfMeasureJpaEntity
import com.example.crm.infrastructure.persistence.repository.UnitOfMeasureJpaRepository
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.UnitOfMeasureResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/units-of-measure")
class UnitOfMeasureController(private val repository: UnitOfMeasureJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<UnitOfMeasureResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("name")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UnitOfMeasureResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("UnitOfMeasure not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    private fun UnitOfMeasureJpaEntity.toResponse() = UnitOfMeasureResponse(
        id = id, code = code, name = name, symbol = symbol,
        isActive = isActive, createdAt = createdAt, updatedAt = updatedAt
    )
}

