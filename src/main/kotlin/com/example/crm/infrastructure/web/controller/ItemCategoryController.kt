package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.ItemCategoryUseCase
import com.example.crm.domain.model.ItemType
import com.example.crm.infrastructure.web.dto.request.ItemCategoryRequest
import com.example.crm.infrastructure.web.dto.response.ItemCategoryListResponse
import com.example.crm.infrastructure.web.dto.response.ItemCategoryResponse
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.mapper.ItemCategoryWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/item-categories")
class ItemCategoryController(
    private val useCase: ItemCategoryUseCase,
    private val mapper: ItemCategoryWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) availableTypes: Set<ItemType>?,
        @RequestParam(required = false) showOnSite: Boolean?
    ): ResponseEntity<PageResponse<ItemCategoryListResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("name"))
        val result = useCase.list(pageable, tenantId, name, availableTypes, showOnSite)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map(mapper::toListResponse),
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ItemCategoryResponse> {
        val category = useCase.getById(id)
        return ResponseEntity.ok(mapper.toResponse(category))
    }

    @PostMapping
    fun create(@RequestBody request: ItemCategoryRequest): ResponseEntity<ItemCategoryResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/item-categories/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ItemCategoryRequest): ResponseEntity<ItemCategoryResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
