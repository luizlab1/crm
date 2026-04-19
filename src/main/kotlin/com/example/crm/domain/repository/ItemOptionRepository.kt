package com.example.crm.domain.repository

import com.example.crm.domain.model.ItemOption

interface ItemOptionRepository {
    fun findByItemId(itemId: Long): List<ItemOption>
    fun findById(id: Long): ItemOption?
    fun save(option: ItemOption): ItemOption
    fun deleteById(id: Long)
}
