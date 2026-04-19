package com.example.crm.application.usecase

import com.example.crm.application.port.input.ItemUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Item
import com.example.crm.domain.model.ItemType
import com.example.crm.domain.repository.ItemRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ItemUseCaseImpl(
    private val itemRepository: ItemRepository
) : ItemUseCase {

    @Transactional(readOnly = true)
    override fun list(
        pageable: Pageable,
        code: UUID?,
        tenantId: Long?,
        categoryId: Long?,
        type: ItemType?,
        name: String?,
        sku: String?,
        isActive: Boolean?
    ): Page<Item> =
        itemRepository.findByFilters(
            code = code,
            tenantId = tenantId,
            categoryId = categoryId,
            type = type,
            name = name,
            sku = sku,
            isActive = isActive,
            pageable = pageable
        )

    @Transactional(readOnly = true)
    override fun getById(id: Long): Item =
        itemRepository.findById(id) ?: throw EntityNotFoundException("Item", id)

    override fun create(item: Item): Item =
        itemRepository.save(item)

    override fun update(id: Long, item: Item): Item {
        val existing = itemRepository.findById(id) ?: throw EntityNotFoundException("Item", id)
        val updated = item.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return itemRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = itemRepository.findById(id) ?: throw EntityNotFoundException("Item", id)
        itemRepository.save(existing.copy(isActive = false))
    }
}

