package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.ItemAdditionalUseCase
import com.example.crm.domain.model.ItemAdditional
import com.example.crm.infrastructure.web.dto.request.AdditionalRequest
import com.example.crm.infrastructure.web.dto.response.AdditionalResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/items/{itemId}/additionals")
class ItemAdditionalController(
    private val useCase: ItemAdditionalUseCase
) {

    @GetMapping
    fun findAll(@PathVariable itemId: Long): ResponseEntity<List<AdditionalResponse>> =
        ResponseEntity.ok(useCase.listByItemId(itemId).map { it.toResponse() })

    @GetMapping("/{id}")
    @Suppress("UnusedParameter")
    fun findById(@PathVariable itemId: Long, @PathVariable id: Long): ResponseEntity<AdditionalResponse> =
        ResponseEntity.ok(useCase.getById(id).toResponse())

    @PostMapping
    fun create(
        @PathVariable itemId: Long,
        @RequestBody request: AdditionalRequest
    ): ResponseEntity<AdditionalResponse> {
        val created = useCase.create(itemId, request.toDomain())
        return ResponseEntity.created(URI.create("/api/v1/items/$itemId/additionals/${created.id}"))
            .body(created.toResponse())
    }

    @PutMapping("/{id}")
    @Suppress("UnusedParameter")
    fun update(
        @PathVariable itemId: Long,
        @PathVariable id: Long,
        @RequestBody request: AdditionalRequest
    ): ResponseEntity<AdditionalResponse> =
        ResponseEntity.ok(useCase.update(id, request.toDomain()).toResponse())

    @DeleteMapping("/{id}")
    @Suppress("UnusedParameter")
    fun delete(@PathVariable itemId: Long, @PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun AdditionalRequest.toDomain() = ItemAdditional(
        name = name, priceCents = priceCents, isActive = isActive
    )

    private fun ItemAdditional.toResponse() = AdditionalResponse(
        id = id, name = name, priceCents = priceCents,
        isActive = isActive, createdAt = createdAt, updatedAt = updatedAt
    )
}
