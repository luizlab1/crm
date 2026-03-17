package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.UnitOfMeasureUseCase
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.UnitOfMeasureResponse
import com.example.crm.infrastructure.web.mapper.UnitOfMeasureWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/units-of-measure")
class UnitOfMeasureController(
    private val useCase: UnitOfMeasureUseCase,
    private val mapper: UnitOfMeasureWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<UnitOfMeasureResponse>> {
        val result = useCase.list(PageRequest.of(page, size, Sort.by("name")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UnitOfMeasureResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))
}
