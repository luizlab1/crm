package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.CountryUseCase
import com.example.crm.infrastructure.web.dto.response.CountryResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.mapper.CountryWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/countries")
class CountryController(
    private val useCase: CountryUseCase,
    private val mapper: CountryWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<CountryResponse>> {
        val result = useCase.list(PageRequest.of(page, size, Sort.by("country")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CountryResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))
}
