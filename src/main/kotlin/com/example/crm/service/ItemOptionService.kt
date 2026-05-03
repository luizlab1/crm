package com.example.crm.service

import com.example.crm.entity.ItemOptionEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.ItemOptionRepository
import com.example.crm.repository.ItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemOptionService(
    private val optionRepository: ItemOptionRepository,
    private val itemRepository: ItemRepository
) {

    @Transactional(readOnly = true)
    fun listByItemId(itemId: Long): List<ItemOptionEntity> {
        itemRepository.findById(itemId).orElseThrow { EntityNotFoundException("Item", itemId) }
        return optionRepository.findByItemId(itemId)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ItemOptionEntity =
        optionRepository.findById(id).orElseThrow { EntityNotFoundException("ItemOption", id) }

    fun create(itemId: Long, entity: ItemOptionEntity): ItemOptionEntity {
        itemRepository.findById(itemId).orElseThrow { EntityNotFoundException("Item", itemId) }
        entity.itemId = itemId
        return optionRepository.save(entity)
    }

    fun update(id: Long, entity: ItemOptionEntity): ItemOptionEntity {
        val existing = getById(id)
        existing.name = entity.name
        existing.priceDeltaCents = entity.priceDeltaCents
        existing.isActive = entity.isActive
        return optionRepository.save(existing)
    }

    fun delete(id: Long) {
        getById(id)
        optionRepository.deleteById(id)
    }
}
