package com.example.crm.service

import com.example.crm.entity.ItemAdditionalEntity
import com.example.crm.entity.ItemEntity
import com.example.crm.entity.ItemOptionEntity
import com.example.crm.entity.ItemProductDatasheetEntity
import com.example.crm.entity.ItemServiceDatasheetEntity
import com.example.crm.entity.ItemTagEntity
import com.example.crm.entity.ItemType
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.ItemAdditionalRepository
import com.example.crm.repository.ItemOptionRepository
import com.example.crm.repository.ItemProductDatasheetRepository
import com.example.crm.repository.ItemRepository
import com.example.crm.repository.ItemServiceDatasheetRepository
import com.example.crm.repository.ItemTagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ItemService(
    private val itemRepository: ItemRepository,
    private val productDatasheetRepository: ItemProductDatasheetRepository,
    private val serviceDatasheetRepository: ItemServiceDatasheetRepository,
    private val tagRepository: ItemTagRepository,
    private val optionRepository: ItemOptionRepository,
    private val additionalRepository: ItemAdditionalRepository
) {

    @Transactional(readOnly = true)
    fun list(
        pageable: Pageable,
        code: UUID? = null,
        tenantId: Long? = null,
        categoryId: Long? = null,
        type: ItemType? = null,
        name: String? = null,
        sku: String? = null,
        isActive: Boolean? = null
    ): Page<ItemEntity> {
        val namePattern = name?.let { "%${it.lowercase()}%" }
        val skuPattern = sku?.let { "%${it.lowercase()}%" }
        return itemRepository.findByFilters(
            code,
            tenantId,
            categoryId,
            type,
            namePattern,
            skuPattern,
            isActive,
            pageable
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ItemEntity =
        itemRepository.findById(id).orElseThrow { EntityNotFoundException("Item", id) }

    @Transactional(readOnly = true)
    fun getProductDatasheet(itemId: Long): ItemProductDatasheetEntity? =
        productDatasheetRepository.findByItemId(itemId).orElse(null)

    @Transactional(readOnly = true)
    fun getServiceDatasheet(itemId: Long): ItemServiceDatasheetEntity? =
        serviceDatasheetRepository.findByItemId(itemId).orElse(null)

    @Transactional(readOnly = true)
    fun getTags(itemId: Long): List<ItemTagEntity> = tagRepository.findByItemId(itemId)

    @Transactional(readOnly = true)
    fun getOptions(itemId: Long): List<ItemOptionEntity> = optionRepository.findByItemId(itemId)

    @Transactional(readOnly = true)
    fun getAdditionals(itemId: Long): List<ItemAdditionalEntity> = additionalRepository.findByItemId(itemId)

    fun create(
        item: ItemEntity,
        productDatasheet: ItemProductDatasheetEntity? = null,
        serviceDatasheet: ItemServiceDatasheetEntity? = null,
        tags: List<String> = emptyList(),
        options: List<ItemOptionEntity> = emptyList(),
        additionals: List<ItemAdditionalEntity> = emptyList()
    ): ItemEntity {
        val saved = itemRepository.save(item)
        saveRelationships(saved.id, productDatasheet, serviceDatasheet, tags, options, additionals)
        return saved
    }

    fun update(
        id: Long,
        item: ItemEntity,
        productDatasheet: ItemProductDatasheetEntity? = null,
        serviceDatasheet: ItemServiceDatasheetEntity? = null,
        tags: List<String> = emptyList(),
        options: List<ItemOptionEntity> = emptyList(),
        additionals: List<ItemAdditionalEntity> = emptyList()
    ): ItemEntity {
        val existing = getById(id)
        existing.tenantId = item.tenantId
        existing.categoryId = item.categoryId
        existing.type = item.type
        existing.name = item.name
        existing.sku = item.sku
        existing.isActive = item.isActive
        val saved = itemRepository.save(existing)
        saveRelationships(saved.id, productDatasheet, serviceDatasheet, tags, options, additionals)
        return saved
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        itemRepository.save(existing)
    }

    private fun saveRelationships(
        itemId: Long,
        productDatasheet: ItemProductDatasheetEntity?,
        serviceDatasheet: ItemServiceDatasheetEntity?,
        tags: List<String>,
        options: List<ItemOptionEntity>,
        additionals: List<ItemAdditionalEntity>
    ) {
        fun upsertProductDatasheet(incoming: ItemProductDatasheetEntity) {
            val existing = productDatasheetRepository.findByItemId(itemId).orElse(null)
            if (existing == null) {
                productDatasheetRepository.save(incoming.also { it.itemId = itemId })
                return
            }

            productDatasheetRepository.save(existing.apply {
                description = incoming.description
                unitPriceCents = incoming.unitPriceCents
                currencyCode = incoming.currencyCode
                unitOfMeasureId = incoming.unitOfMeasureId
                weightKg = incoming.weightKg
                volumeM3 = incoming.volumeM3
                densityKgM3 = incoming.densityKgM3
                heightCm = incoming.heightCm
                widthCm = incoming.widthCm
                lengthCm = incoming.lengthCm
            })
        }

        fun upsertServiceDatasheet(incoming: ItemServiceDatasheetEntity) {
            val existing = serviceDatasheetRepository.findByItemId(itemId).orElse(null)
            if (existing == null) {
                serviceDatasheetRepository.save(incoming.also { it.itemId = itemId })
                return
            }

            serviceDatasheetRepository.save(existing.apply {
                description = incoming.description
                unitPriceCents = incoming.unitPriceCents
                currencyCode = incoming.currencyCode
                durationMinutes = incoming.durationMinutes
                requiresStaff = incoming.requiresStaff
                bufferMinutes = incoming.bufferMinutes
            })
        }

        productDatasheet?.let { upsertProductDatasheet(it) }
        serviceDatasheet?.let { upsertServiceDatasheet(it) }
        tagRepository.deleteByItemId(itemId)
        tags.forEach { tag -> tagRepository.save(ItemTagEntity(itemId = itemId, tag = tag)) }
        optionRepository.deleteByItemId(itemId)
        options.forEach { opt ->
            optionRepository.save(ItemOptionEntity(
                itemId = itemId,
                name = opt.name,
                priceDeltaCents = opt.priceDeltaCents,
                isActive = opt.isActive
            ))
        }
        additionalRepository.deleteByItemId(itemId)
        additionals.forEach { add ->
            additionalRepository.save(ItemAdditionalEntity(
                itemId = itemId,
                name = add.name,
                priceCents = add.priceCents,
                isActive = add.isActive
            ))
        }
    }
}
