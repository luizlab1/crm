package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.AddressUseCase
import com.example.crm.infrastructure.web.dto.response.AddressResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.mapper.AddressWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/addresses")
class AddressController(
    private val useCase: AddressUseCase,
    private val mapper: AddressWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<AddressResponse>> {
        val result = useCase.list(PageRequest.of(page, size, Sort.by("id")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<AddressResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))
}
