package com.example.crm.controller

import com.example.crm.dto.response.UnitOfMeasureResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.service.UnitOfMeasureService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/units-of-measure")
class UnitOfMeasureController(
    private val service: UnitOfMeasureService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<UnitOfMeasureResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("name")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map {
                UnitOfMeasureResponse(
                    it.id, it.code, it.name, it.symbol, it.isActive, it.createdAt, it.updatedAt
                )
            },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UnitOfMeasureResponse> {
        val it = service.getById(id)
        return ResponseEntity.ok(
            UnitOfMeasureResponse(it.id, it.code, it.name, it.symbol, it.isActive, it.createdAt, it.updatedAt)
        )
}
}
