package com.example.crm.application.usecase

import com.example.crm.application.port.input.ItemAdditionalUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.ItemAdditional
import com.example.crm.domain.repository.ItemAdditionalRepository
import com.example.crm.domain.repository.ItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemAdditionalUseCaseImpl(
    private val additionalRepository: ItemAdditionalRepository,
    private val itemRepository: ItemRepository
) : ItemAdditionalUseCase {

    @Transactional(readOnly = true)
    override fun listByItemId(itemId: Long): List<ItemAdditional> {
        itemRepository.findById(itemId) ?: throw EntityNotFoundException("Item", itemId)
        return additionalRepository.findByItemId(itemId)
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ItemAdditional =
        additionalRepository.findById(id) ?: throw EntityNotFoundException("ItemAdditional", id)

    override fun create(itemId: Long, additional: ItemAdditional): ItemAdditional {
        itemRepository.findById(itemId) ?: throw EntityNotFoundException("Item", itemId)
        return additionalRepository.save(additional.copy(itemId = itemId))
    }

    override fun update(id: Long, additional: ItemAdditional): ItemAdditional {
        val existing = additionalRepository.findById(id) ?: throw EntityNotFoundException("ItemAdditional", id)
        return additionalRepository.save(
            additional.copy(id = existing.id, itemId = existing.itemId, createdAt = existing.createdAt)
        )
    }

    override fun delete(id: Long) {
        additionalRepository.findById(id) ?: throw EntityNotFoundException("ItemAdditional", id)
        additionalRepository.deleteById(id)
    }
}
