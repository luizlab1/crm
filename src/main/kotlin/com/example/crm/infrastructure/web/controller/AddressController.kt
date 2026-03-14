package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.AddressJpaEntity
import com.example.crm.infrastructure.persistence.repository.AddressJpaRepository
import com.example.crm.infrastructure.web.dto.request.AddressRequest
import com.example.crm.infrastructure.web.dto.response.AddressResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/addresses")
class AddressController(private val repository: AddressJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<AddressResponse>> {
        val result = repository.findAll(PageRequest.of(page, size, Sort.by("id")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<AddressResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Address not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: AddressRequest): ResponseEntity<AddressResponse> {
        val entity = request.toEntity()
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/addresses/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: AddressRequest): ResponseEntity<AddressResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Address not found: $id") }
        entity.street = request.street
        entity.number = request.number
        entity.complement = request.complement
        entity.neighborhood = request.neighborhood
        entity.cityId = request.cityId
        entity.postalCode = request.postalCode
        entity.latitude = request.latitude
        entity.longitude = request.longitude
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Address not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun AddressRequest.toEntity() = AddressJpaEntity(
        street = street, number = number, complement = complement,
        neighborhood = neighborhood, cityId = cityId, postalCode = postalCode,
        latitude = latitude, longitude = longitude, isActive = isActive
    )

    private fun AddressJpaEntity.toResponse() = AddressResponse(
        id = id, street = street, number = number, complement = complement,
        neighborhood = neighborhood, cityId = cityId, postalCode = postalCode,
        latitude = latitude, longitude = longitude, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

