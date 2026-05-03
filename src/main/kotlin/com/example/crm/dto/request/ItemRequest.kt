package com.example.crm.dto.request

import com.example.crm.entity.ItemType
import java.math.BigDecimal

data class ItemRequest(
    val tenantId: Long,
    val categoryId: Long? = null,
    val type: ItemType,
    val name: String,
    val sku: String? = null,
    val isActive: Boolean = true,
    val productDatasheet: ProductDatasheetRequest? = null,
    val serviceDatasheet: ServiceDatasheetRequest? = null,
    val tags: List<String> = emptyList(),
    val options: List<OptionRequest> = emptyList(),
    val additionals: List<AdditionalRequest> = emptyList()
)

data class ProductDatasheetRequest(
    val description: String? = null,
    val unitPriceCents: Long = 0,
    val currencyCode: String = "BRL",
    val unitOfMeasureId: Long? = null,
    val weightKg: BigDecimal? = null,
    val volumeM3: BigDecimal? = null,
    val densityKgM3: BigDecimal? = null,
    val heightCm: BigDecimal? = null,
    val widthCm: BigDecimal? = null,
    val lengthCm: BigDecimal? = null
)

data class ServiceDatasheetRequest(
    val description: String? = null,
    val unitPriceCents: Long = 0,
    val currencyCode: String = "BRL",
    val durationMinutes: Int? = null,
    val requiresStaff: Boolean = false,
    val bufferMinutes: Int? = null
)

data class OptionRequest(
    val name: String,
    val priceDeltaCents: Long = 0,
    val isActive: Boolean = true
)

data class AdditionalRequest(
    val name: String,
    val priceCents: Long = 0,
    val isActive: Boolean = true
)
