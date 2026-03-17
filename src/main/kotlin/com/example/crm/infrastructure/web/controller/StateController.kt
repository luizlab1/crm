package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.StateUseCase
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.StateResponse
import com.example.crm.infrastructure.web.mapper.StateWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/states")
class StateController(
    private val useCase: StateUseCase,
    private val mapper: StateWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<StateResponse>> {
        val result = useCase.list(PageRequest.of(page, size, Sort.by("state")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<StateResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @GetMapping("/country/{countryId}")
    fun findByCountry(@PathVariable countryId: Long): ResponseEntity<List<StateResponse>> =
        ResponseEntity.ok(useCase.findByCountryId(countryId).map { mapper.toResponse(it) })
}
