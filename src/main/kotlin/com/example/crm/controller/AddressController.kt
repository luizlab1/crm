package com.example.crm.controller

import com.example.crm.dto.response.AddressResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.service.AddressService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/addresses")
class AddressController(
    private val service: AddressService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<AddressResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("id")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map {
                AddressResponse(
                    id = it.id, street = it.street, number = it.number,
                    complement = it.complement, neighborhood = it.neighborhood,
                    cityId = it.cityId, postalCode = it.postalCode,
                    latitude = it.latitude, longitude = it.longitude,
                    isActive = it.isActive, createdAt = it.createdAt, updatedAt = it.updatedAt
                )
            },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<AddressResponse> {
        val it = service.getById(id)
        return ResponseEntity.ok(AddressResponse(
            id = it.id, street = it.street, number = it.number,
            complement = it.complement, neighborhood = it.neighborhood,
            cityId = it.cityId, postalCode = it.postalCode,
            latitude = it.latitude, longitude = it.longitude,
            isActive = it.isActive, createdAt = it.createdAt, updatedAt = it.updatedAt
        ))
    }
}
