package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.CityJpaEntity
import com.example.crm.infrastructure.persistence.repository.CityJpaRepository
import com.example.crm.infrastructure.web.dto.response.CityResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/cities")
class CityController(private val repository: CityJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<CityResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("city")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CityResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("City not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @GetMapping("/state/{stateId}")
    fun findByState(@PathVariable stateId: Long): ResponseEntity<List<CityResponse>> {
        val cities = repository.findByStateId(stateId).map { it.toResponse() }
        return ResponseEntity.ok(cities)
    }

    private fun CityJpaEntity.toResponse() = CityResponse(
        id = id, stateId = stateId, city = city,
        ibgeCode = ibgeCode, createdAt = createdAt, updatedAt = updatedAt
    )
}

