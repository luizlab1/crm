package com.example.crm.application.usecase

import com.example.crm.application.port.input.ItemCategoryUseCase
import com.example.crm.domain.exception.EntityHasDependenciesException
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.ItemCategory
import com.example.crm.domain.model.ItemCategoryPatch
import com.example.crm.domain.model.ItemType
import com.example.crm.domain.repository.ItemCategoryRepository
import com.example.crm.domain.repository.ItemRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemCategoryUseCaseImpl(
    private val itemCategoryRepository: ItemCategoryRepository,
    private val itemRepository: ItemRepository
) : ItemCategoryUseCase {

    @Transactional(readOnly = true)
    override fun list(
        pageable: Pageable,
        tenantId: Long?,
        name: String?,
        availableTypes: Set<ItemType>?,
        showOnSite: Boolean?,
        isActive: Boolean?
    ): Page<ItemCategory> =
        itemCategoryRepository.findByFilters(tenantId, name, availableTypes, showOnSite, isActive, pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): ItemCategory =
        itemCategoryRepository.findById(id) ?: throw EntityNotFoundException("ItemCategory", id)

    override fun create(itemCategory: ItemCategory): ItemCategory =
        itemCategoryRepository.save(itemCategory)

    override fun update(id: Long, itemCategory: ItemCategory): ItemCategory {
        val existing = itemCategoryRepository.findById(id) ?: throw EntityNotFoundException("ItemCategory", id)
        val updated = itemCategory.copy(id = existing.id, createdAt = existing.createdAt)
        return itemCategoryRepository.save(updated)
    }

    override fun patch(id: Long, patch: ItemCategoryPatch): ItemCategory {
        val existing = itemCategoryRepository.findById(id) ?: throw EntityNotFoundException("ItemCategory", id)
        val updated = existing.copy(
            tenantId = patch.tenantId ?: existing.tenantId,
            name = patch.name ?: existing.name,
            description = patch.description ?: existing.description,
            showOnSite = patch.showOnSite ?: existing.showOnSite,
            sortOrder = patch.sortOrder ?: existing.sortOrder,
            isActive = patch.isActive ?: existing.isActive,
            availableTypes = patch.availableTypes ?: existing.availableTypes
        )
        return itemCategoryRepository.save(updated)
    }

    override fun updateSortOrders(sortOrders: Map<Long, Int>): List<ItemCategory> =
        sortOrders.map { (id, sortOrder) ->
            val existing = itemCategoryRepository.findById(id) ?: throw EntityNotFoundException("ItemCategory", id)
            itemCategoryRepository.save(existing.copy(sortOrder = sortOrder))
        }

    override fun delete(id: Long) {
        itemCategoryRepository.findById(id) ?: throw EntityNotFoundException("ItemCategory", id)
        val itemCount = itemRepository.countByCategoryId(id)
        if (itemCount > 0) {
            throw EntityHasDependenciesException("ItemCategory", id, "items", itemCount)
        }
        itemCategoryRepository.deleteById(id)
    }
}

