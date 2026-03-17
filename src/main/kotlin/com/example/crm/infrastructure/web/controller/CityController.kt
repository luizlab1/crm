package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.CityUseCase
import com.example.crm.infrastructure.web.dto.response.CityResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.mapper.CityWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/cities")
class CityController(
    private val useCase: CityUseCase,
    private val mapper: CityWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<CityResponse>> {
        val result = useCase.list(PageRequest.of(page, size, Sort.by("city")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CityResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @GetMapping("/state/{stateId}")
    fun findByState(@PathVariable stateId: Long): ResponseEntity<List<CityResponse>> =
        ResponseEntity.ok(useCase.findByStateId(stateId).map { mapper.toResponse(it) })
}
