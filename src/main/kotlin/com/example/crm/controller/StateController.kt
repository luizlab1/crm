package com.example.crm.controller

import com.example.crm.dto.response.StateResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.service.StateService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/states")
class StateController(
    private val service: StateService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<StateResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("state")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { StateResponse(it.id, it.countryId, it.acronym, it.state, it.ibgeCode, it.createdAt, it.updatedAt) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<StateResponse> {
        val it = service.getById(id)
        return ResponseEntity.ok(StateResponse(it.id, it.countryId, it.acronym, it.state, it.ibgeCode, it.createdAt, it.updatedAt))
    }

    @GetMapping("/country/{countryId}")
    fun findByCountry(@PathVariable countryId: Long): ResponseEntity<List<StateResponse>> =
        ResponseEntity.ok(service.findByCountryId(countryId).map { StateResponse(it.id, it.countryId, it.acronym, it.state, it.ibgeCode, it.createdAt, it.updatedAt) })
}
