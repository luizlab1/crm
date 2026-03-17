package com.example.crm.application.usecase

import com.example.crm.application.port.input.ItemCategoryUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.ItemCategory
import com.example.crm.domain.repository.ItemCategoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemCategoryUseCaseImpl(
    private val itemCategoryRepository: ItemCategoryRepository
) : ItemCategoryUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<ItemCategory> =
        if (tenantId != null) itemCategoryRepository.findByTenantId(tenantId, pageable)
        else itemCategoryRepository.findAll(pageable)

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

    override fun delete(id: Long) {
        itemCategoryRepository.findById(id) ?: throw EntityNotFoundException("ItemCategory", id)
        itemCategoryRepository.deleteById(id)
    }
}

