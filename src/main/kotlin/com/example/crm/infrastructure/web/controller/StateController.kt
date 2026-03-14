package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.StateJpaEntity
import com.example.crm.infrastructure.persistence.repository.StateJpaRepository
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.StateResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/states")
class StateController(private val repository: StateJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<StateResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("state")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<StateResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("State not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @GetMapping("/country/{countryId}")
    fun findByCountry(@PathVariable countryId: Long): ResponseEntity<List<StateResponse>> {
        val states = repository.findByCountryId(countryId).map { it.toResponse() }
        return ResponseEntity.ok(states)
    }

    private fun StateJpaEntity.toResponse() = StateResponse(
        id = id, countryId = countryId, acronym = acronym, state = state,
        ibgeCode = ibgeCode, createdAt = createdAt, updatedAt = updatedAt
    )
}

