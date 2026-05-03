package com.example.crm.controller

import com.example.crm.dto.response.CountryResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.service.CountryService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/countries")
class CountryController(
    private val service: CountryService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<CountryResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("country")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map {
                CountryResponse(
                    it.id, it.iso2, it.iso3, it.country, it.createdAt, it.updatedAt
                )
            },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CountryResponse> {
        val it = service.getById(id)
        return ResponseEntity.ok(
            CountryResponse(it.id, it.iso2, it.iso3, it.country, it.createdAt, it.updatedAt)
        )
    }
}
