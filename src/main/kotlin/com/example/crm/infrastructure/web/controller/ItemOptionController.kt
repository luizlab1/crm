package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.ItemOptionUseCase
import com.example.crm.domain.model.ItemOption
import com.example.crm.infrastructure.web.dto.request.OptionRequest
import com.example.crm.infrastructure.web.dto.response.OptionResponse
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
@RequestMapping("/api/v1/items/{itemId}/options")
class ItemOptionController(
    private val useCase: ItemOptionUseCase
) {

    @GetMapping
    fun findAll(@PathVariable itemId: Long): ResponseEntity<List<OptionResponse>> =
        ResponseEntity.ok(useCase.listByItemId(itemId).map { it.toResponse() })

    @GetMapping("/{id}")
    @Suppress("UnusedParameter")
    fun findById(@PathVariable itemId: Long, @PathVariable id: Long): ResponseEntity<OptionResponse> =
        ResponseEntity.ok(useCase.getById(id).toResponse())

    @PostMapping
    fun create(
        @PathVariable itemId: Long,
        @RequestBody request: OptionRequest
    ): ResponseEntity<OptionResponse> {
        val created = useCase.create(itemId, request.toDomain())
        return ResponseEntity.created(URI.create("/api/v1/items/$itemId/options/${created.id}"))
            .body(created.toResponse())
    }

    @PutMapping("/{id}")
    @Suppress("UnusedParameter")
    fun update(
        @PathVariable itemId: Long,
        @PathVariable id: Long,
        @RequestBody request: OptionRequest
    ): ResponseEntity<OptionResponse> =
        ResponseEntity.ok(useCase.update(id, request.toDomain()).toResponse())

    @DeleteMapping("/{id}")
    @Suppress("UnusedParameter")
    fun delete(@PathVariable itemId: Long, @PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun OptionRequest.toDomain() = ItemOption(
        name = name, priceDeltaCents = priceDeltaCents, isActive = isActive
    )

    private fun ItemOption.toResponse() = OptionResponse(
        id = id, name = name, priceDeltaCents = priceDeltaCents,
        isActive = isActive, createdAt = createdAt, updatedAt = updatedAt
    )
}
