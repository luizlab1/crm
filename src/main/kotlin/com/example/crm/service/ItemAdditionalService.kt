package com.example.crm.service

import com.example.crm.entity.ItemAdditionalEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.ItemAdditionalRepository
import com.example.crm.repository.ItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemAdditionalService(
    private val additionalRepository: ItemAdditionalRepository,
    private val itemRepository: ItemRepository
) {

    @Transactional(readOnly = true)
    fun listByItemId(itemId: Long): List<ItemAdditionalEntity> {
        itemRepository.findById(itemId).orElseThrow { EntityNotFoundException("Item", itemId) }
        return additionalRepository.findByItemId(itemId)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ItemAdditionalEntity =
        additionalRepository.findById(id).orElseThrow { EntityNotFoundException("ItemAdditional", id) }

    fun create(itemId: Long, entity: ItemAdditionalEntity): ItemAdditionalEntity {
        itemRepository.findById(itemId).orElseThrow { EntityNotFoundException("Item", itemId) }
        entity.itemId = itemId
        return additionalRepository.save(entity)
    }

    fun update(id: Long, entity: ItemAdditionalEntity): ItemAdditionalEntity {
        val existing = getById(id)
        existing.name = entity.name
        existing.priceCents = entity.priceCents
        existing.isActive = entity.isActive
        return additionalRepository.save(existing)
    }

    fun delete(id: Long) {
        getById(id)
        additionalRepository.deleteById(id)
    }
}
