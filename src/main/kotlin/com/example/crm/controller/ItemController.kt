package com.example.crm.controller

import com.example.crm.dto.request.ItemRequest
import com.example.crm.dto.response.*
import com.example.crm.entity.*
import com.example.crm.service.ItemService
import com.example.crm.service.UploadService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/items")
class ItemController(
    private val service: ItemService,
    private val uploadService: UploadService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) type: ItemType?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) sku: String?,
        @RequestParam(required = false) isActive: Boolean?
    ): ResponseEntity<PageResponse<ItemListResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("name")), tenantId = tenantId,
            categoryId = categoryId, type = type, name = name, sku = sku, isActive = isActive)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toListResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ItemResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: ItemRequest): ResponseEntity<ItemResponse> {
        val created = service.create(
            item = request.toEntity(),
            productDatasheet = request.productDatasheet?.toEntity(),
            serviceDatasheet = request.serviceDatasheet?.toEntity(),
            tags = request.tags,
            options = request.options.map { ItemOptionEntity(name = it.name, priceDeltaCents = it.priceDeltaCents, isActive = it.isActive) },
            additionals = request.additionals.map { ItemAdditionalEntity(name = it.name, priceCents = it.priceCents, isActive = it.isActive) }
        )
        return ResponseEntity.created(URI.create("/api/v1/items/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ItemRequest): ResponseEntity<ItemResponse> {
        val updated = service.update(
            id = id,
            item = request.toEntity(),
            productDatasheet = request.productDatasheet?.toEntity(),
            serviceDatasheet = request.serviceDatasheet?.toEntity(),
            tags = request.tags,
            options = request.options.map { ItemOptionEntity(name = it.name, priceDeltaCents = it.priceDeltaCents, isActive = it.isActive) },
            additionals = request.additionals.map { ItemAdditionalEntity(name = it.name, priceCents = it.priceCents, isActive = it.isActive) }
        )
        return ResponseEntity.ok(updated.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun ItemRequest.toEntity() = ItemEntity(
        tenantId = tenantId, categoryId = categoryId, type = type, name = name, sku = sku, isActive = isActive
    )

    private fun com.example.crm.dto.request.ProductDatasheetRequest.toEntity() = ItemProductDatasheetEntity(
        description = description, unitPriceCents = unitPriceCents, currencyCode = currencyCode,
        unitOfMeasureId = unitOfMeasureId, weightKg = weightKg, volumeM3 = volumeM3,
        densityKgM3 = densityKgM3, heightCm = heightCm, widthCm = widthCm, lengthCm = lengthCm
    )

    private fun com.example.crm.dto.request.ServiceDatasheetRequest.toEntity() = ItemServiceDatasheetEntity(
        description = description, unitPriceCents = unitPriceCents, currencyCode = currencyCode,
        durationMinutes = durationMinutes, requiresStaff = requiresStaff, bufferMinutes = bufferMinutes
    )

    private fun ItemEntity.toListResponse() = ItemListResponse(
        id = id, code = code, tenantId = tenantId, categoryId = categoryId, type = type,
        name = name, sku = sku, isActive = isActive,
        photos = resolvePhotos(id, type),
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun ItemEntity.toResponse(): ItemResponse {
        val productDs = service.getProductDatasheet(id)
        val serviceDs = service.getServiceDatasheet(id)
        val tags = service.getTags(id).map { it.tag }
        val options = service.getOptions(id).map { it.toResponse() }
        val additionals = service.getAdditionals(id).map { it.toResponse() }
        return ItemResponse(
            id = id, code = code, tenantId = tenantId, categoryId = categoryId, type = type,
            name = name, sku = sku, isActive = isActive,
            photos = resolvePhotos(id, type),
            productDatasheet = productDs?.toResponse(),
            serviceDatasheet = serviceDs?.toResponse(),
            tags = tags, options = options, additionals = additionals,
            createdAt = createdAt, updatedAt = updatedAt
        )
    }

    private fun ItemProductDatasheetEntity.toResponse() = ProductDatasheetResponse(
        id = id, description = description, unitPriceCents = unitPriceCents, currencyCode = currencyCode,
        unitOfMeasureId = unitOfMeasureId, weightKg = weightKg, volumeM3 = volumeM3,
        densityKgM3 = densityKgM3, heightCm = heightCm, widthCm = widthCm, lengthCm = lengthCm,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun ItemServiceDatasheetEntity.toResponse() = ServiceDatasheetResponse(
        id = id, description = description, unitPriceCents = unitPriceCents, currencyCode = currencyCode,
        durationMinutes = durationMinutes, requiresStaff = requiresStaff, bufferMinutes = bufferMinutes,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun ItemOptionEntity.toResponse() = OptionResponse(
        id = id, name = name, priceDeltaCents = priceDeltaCents, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun ItemAdditionalEntity.toResponse() = AdditionalResponse(
        id = id, name = name, priceCents = priceCents, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun resolvePhotos(entityId: Long, type: ItemType): List<String> = try {
        val fileType = if (type == ItemType.SERVICE) FileType.SERVICE else FileType.PRODUCT
        val base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().removeSuffix("/")
        uploadService.list(fileType, entityId, 0, 20).map { "$base/api/v1/uploads/${it.id}/view" }
    } catch (e: Exception) { emptyList() }
}
