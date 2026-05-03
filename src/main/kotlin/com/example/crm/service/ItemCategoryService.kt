package com.example.crm.service

import com.example.crm.entity.*
import com.example.crm.exception.EntityHasDependenciesException
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.ItemCategoryRepository
import com.example.crm.repository.ItemRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemCategoryService(
    private val itemCategoryRepository: ItemCategoryRepository,
    private val itemRepository: ItemRepository
) {

    @Transactional(readOnly = true)
    fun list(
        pageable: Pageable,
        tenantId: Long?,
        name: String?,
        showOnSite: Boolean?,
        active: Boolean?
    ): Page<ItemCategoryEntity> {
        val namePattern = name?.let { "%${it.lowercase()}%" }
        return itemCategoryRepository.findByFilters(tenantId, namePattern, showOnSite, active, pageable)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ItemCategoryEntity =
        itemCategoryRepository.findById(id).orElseThrow { EntityNotFoundException("ItemCategory", id) }

    fun create(entity: ItemCategoryEntity): ItemCategoryEntity = itemCategoryRepository.save(entity)

    fun update(id: Long, entity: ItemCategoryEntity): ItemCategoryEntity {
        val existing = getById(id)
        existing.tenantId = entity.tenantId
        existing.name = entity.name
        existing.description = entity.description
        existing.showOnSite = entity.showOnSite
        existing.sortOrder = entity.sortOrder
        existing.active = entity.active
        existing.availableTypes = entity.availableTypes
        return itemCategoryRepository.save(existing)
    }

    fun patch(
        id: Long,
        tenantId: Long? = null,
        name: String? = null,
        description: String? = null,
        showOnSite: Boolean? = null,
        sortOrder: Int? = null,
        active: Boolean? = null,
        availableTypes: MutableSet<ItemType>? = null
    ): ItemCategoryEntity {
        val existing = getById(id)
        tenantId?.let { existing.tenantId = it }
        name?.let { existing.name = it }
        description?.let { existing.description = it }
        showOnSite?.let { existing.showOnSite = it }
        sortOrder?.let { existing.sortOrder = it }
        active?.let { existing.active = it }
        availableTypes?.let { existing.availableTypes = it }
        return itemCategoryRepository.save(existing)
    }

    fun updateSortOrders(sortOrders: Map<Long, Int>): List<ItemCategoryEntity> =
        sortOrders.map { (id, sortOrder) ->
            val existing = getById(id)
            existing.sortOrder = sortOrder
            itemCategoryRepository.save(existing)
        }

    fun delete(id: Long) {
        getById(id)
        val itemCount = itemRepository.countByCategoryId(id)
        if (itemCount > 0) throw EntityHasDependenciesException("ItemCategory", id, "items", itemCount)
        itemCategoryRepository.deleteById(id)
    }
}
