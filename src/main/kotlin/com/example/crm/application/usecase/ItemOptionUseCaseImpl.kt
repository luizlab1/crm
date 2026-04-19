package com.example.crm.application.usecase

import com.example.crm.application.port.input.ItemOptionUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.ItemOption
import com.example.crm.domain.repository.ItemOptionRepository
import com.example.crm.domain.repository.ItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemOptionUseCaseImpl(
    private val optionRepository: ItemOptionRepository,
    private val itemRepository: ItemRepository
) : ItemOptionUseCase {

    @Transactional(readOnly = true)
    override fun listByItemId(itemId: Long): List<ItemOption> {
        itemRepository.findById(itemId) ?: throw EntityNotFoundException("Item", itemId)
        return optionRepository.findByItemId(itemId)
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ItemOption =
        optionRepository.findById(id) ?: throw EntityNotFoundException("ItemOption", id)

    override fun create(itemId: Long, option: ItemOption): ItemOption {
        itemRepository.findById(itemId) ?: throw EntityNotFoundException("Item", itemId)
        return optionRepository.save(option.copy(itemId = itemId))
    }

    override fun update(id: Long, option: ItemOption): ItemOption {
        val existing = optionRepository.findById(id) ?: throw EntityNotFoundException("ItemOption", id)
        return optionRepository.save(
            option.copy(id = existing.id, itemId = existing.itemId, createdAt = existing.createdAt)
        )
    }

    override fun delete(id: Long) {
        optionRepository.findById(id) ?: throw EntityNotFoundException("ItemOption", id)
        optionRepository.deleteById(id)
    }
}
