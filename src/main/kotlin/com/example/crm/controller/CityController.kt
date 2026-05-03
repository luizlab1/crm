package com.example.crm.controller

import com.example.crm.dto.response.CityResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.service.CityService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/cities")
class CityController(
    private val service: CityService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<CityResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("city")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map {
                CityResponse(
                    it.id, it.stateId, it.city, it.ibgeCode, it.createdAt, it.updatedAt
                )
            },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CityResponse> {
        val it = service.getById(id)
        return ResponseEntity.ok(
            CityResponse(it.id, it.stateId, it.city, it.ibgeCode, it.createdAt, it.updatedAt)
        )
    }

    @GetMapping("/state/{stateId}")
    fun findByState(@PathVariable stateId: Long): ResponseEntity<List<CityResponse>> =
        ResponseEntity.ok(
            service.findByStateId(stateId).map {
                CityResponse(
                    it.id, it.stateId, it.city, it.ibgeCode, it.createdAt, it.updatedAt
                )
            }
        )
}
