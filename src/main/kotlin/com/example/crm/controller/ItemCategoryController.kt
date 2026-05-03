package com.example.crm.controller

import com.example.crm.dto.request.ItemCategoryPatchRequest
import com.example.crm.dto.request.ItemCategoryRequest
import com.example.crm.dto.request.ItemCategorySortOrderRequest
import com.example.crm.dto.response.ItemCategoryListResponse
import com.example.crm.dto.response.ItemCategoryResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.entity.FileType
import com.example.crm.entity.ItemCategoryEntity
import com.example.crm.entity.ItemType
import com.example.crm.service.ItemCategoryService
import com.example.crm.service.UploadService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/api/v1/item-categories")
class ItemCategoryController(
    private val service: ItemCategoryService,
    private val uploadService: UploadService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) showOnSite: Boolean?,
        @RequestParam(required = false) active: Boolean?
    ): ResponseEntity<PageResponse<ItemCategoryListResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("sortOrder")), tenantId, name, showOnSite, active)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toListResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ItemCategoryResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: ItemCategoryRequest): ResponseEntity<ItemCategoryResponse> {
        val created = service.create(request.toEntity())
        return ResponseEntity.created(URI.create("/api/v1/item-categories/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ItemCategoryRequest): ResponseEntity<ItemCategoryResponse> =
        ResponseEntity.ok(service.update(id, request.toEntity()).toResponse())

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody request: ItemCategoryPatchRequest): ResponseEntity<ItemCategoryResponse> =
        ResponseEntity.ok(service.patch(
            id = id,
            tenantId = request.tenantId,
            name = request.name,
            description = request.description,
            showOnSite = request.showOnSite,
            sortOrder = request.sortOrder,
            active = request.active,
            availableTypes = request.availableTypes?.toMutableSet()
        ).toResponse())

    @PutMapping("/sort-order")
    fun updateSortOrders(@RequestBody request: ItemCategorySortOrderRequest): ResponseEntity<List<ItemCategoryResponse>> {
        val sortMap = request.items.associate { it.id to it.sortOrder }
        return ResponseEntity.ok(service.updateSortOrders(sortMap).map { it.toResponse() })
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun ItemCategoryRequest.toEntity() = ItemCategoryEntity(
        tenantId = tenantId, name = name, description = description,
        showOnSite = showOnSite, sortOrder = sortOrder ?: 0,
        active = active, availableTypes = availableTypes.toMutableSet()
    )

    private fun ItemCategoryEntity.toResponse() = ItemCategoryResponse(
        id = id, tenantId = tenantId, name = name, description = description,
        showOnSite = showOnSite, sortOrder = sortOrder, active = active,
        availableTypes = availableTypes, createdAt = createdAt, updatedAt = updatedAt,
        photo = resolvePhoto(id)
    )

    private fun ItemCategoryEntity.toListResponse() = ItemCategoryListResponse(
        id = id, tenantId = tenantId, name = name, description = description,
        showOnSite = showOnSite, sortOrder = sortOrder, active = active,
        availableTypes = availableTypes, createdAt = createdAt, updatedAt = updatedAt,
        photo = resolvePhoto(id)
    )

    private fun resolvePhoto(entityId: Long): String? = runCatching {
        uploadService.list(FileType.CATEGORY, entityId, 0, 1).firstOrNull()?.id?.let { uploadId ->
            val base = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString()
                .removeSuffix("/")
            "$base/api/v1/uploads/$uploadId/view"
        }
    }.getOrNull()
}
