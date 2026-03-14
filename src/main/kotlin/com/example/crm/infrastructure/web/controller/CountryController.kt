package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.CountryJpaEntity
import com.example.crm.infrastructure.persistence.repository.CountryJpaRepository
import com.example.crm.infrastructure.web.dto.response.CountryResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/countries")
class CountryController(private val repository: CountryJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<CountryResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("country")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CountryResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Country not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    private fun CountryJpaEntity.toResponse() = CountryResponse(
        id = id, iso2 = iso2, iso3 = iso3, country = country,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

