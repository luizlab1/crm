package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.ItemUseCase
import com.example.crm.domain.model.ItemType
import com.example.crm.infrastructure.web.dto.request.ItemRequest
import com.example.crm.infrastructure.web.dto.response.ItemListResponse
import com.example.crm.infrastructure.web.dto.response.ItemResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.mapper.ItemWebMapper
import com.example.crm.infrastructure.web.mapper.ItemPhotosResolver
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/items")
class ItemController(
    private val useCase: ItemUseCase,
    private val mapper: ItemWebMapper,
    private val itemPhotosResolver: ItemPhotosResolver
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) code: UUID?,
        @RequestParam(required = false) tenantId: Long?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) type: ItemType?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) sku: String?,
        @RequestParam(name = "active", required = false) isActive: Boolean?
    ): ResponseEntity<PageResponse<ItemListResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("name"))
        val result = useCase.list(pageable, code, tenantId, categoryId, type, name, sku, isActive)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { item ->
                mapper.toListResponse(item).copy(
                    photos = listOfNotNull(itemPhotosResolver.resolveMain(item.type, item.id))
                )
            },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ItemResponse> =
        useCase.getById(id).let { item ->
            ResponseEntity.ok(
                mapper.toResponse(item).copy(
                    photos = itemPhotosResolver.resolve(item.type, item.id)
                )
            )
        }

    @PostMapping
    fun create(@RequestBody request: ItemRequest): ResponseEntity<ItemResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/items/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ItemRequest): ResponseEntity<ItemResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
