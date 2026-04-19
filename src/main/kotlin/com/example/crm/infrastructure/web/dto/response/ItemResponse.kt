package com.example.crm.infrastructure.web.dto.response

import com.example.crm.domain.model.ItemType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class ItemListResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val categoryId: Long?,
    val type: ItemType,
    val name: String,
    val sku: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class ItemResponse(
    val id: Long,
    val code: UUID,
    val tenantId: Long,
    val categoryId: Long?,
    val type: ItemType,
    val name: String,
    val sku: String?,
    val isActive: Boolean,
    val photos: List<String> = emptyList(),
    val productDatasheet: ProductDatasheetResponse? = null,
    val serviceDatasheet: ServiceDatasheetResponse? = null,
    val tags: List<String> = emptyList(),
    val options: List<OptionResponse> = emptyList(),
    val additionals: List<AdditionalResponse> = emptyList(),
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class ProductDatasheetResponse(
    val id: Long,
    val description: String? = null,
    val unitPriceCents: Long = 0,
    val currencyCode: String = "BRL",
    val unitOfMeasureId: Long? = null,
    val weightKg: BigDecimal? = null,
    val volumeM3: BigDecimal? = null,
    val densityKgM3: BigDecimal? = null,
    val heightCm: BigDecimal? = null,
    val widthCm: BigDecimal? = null,
    val lengthCm: BigDecimal? = null,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class ServiceDatasheetResponse(
    val id: Long,
    val description: String? = null,
    val unitPriceCents: Long = 0,
    val currencyCode: String = "BRL",
    val durationMinutes: Int? = null,
    val requiresStaff: Boolean = false,
    val bufferMinutes: Int? = null,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class OptionResponse(
    val id: Long,
    val name: String,
    val priceDeltaCents: Long = 0,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class AdditionalResponse(
    val id: Long,
    val name: String,
    val priceCents: Long = 0,
    val isActive: Boolean = true,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
