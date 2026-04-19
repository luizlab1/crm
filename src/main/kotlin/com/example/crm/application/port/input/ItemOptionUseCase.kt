package com.example.crm.application.port.input

import com.example.crm.domain.model.ItemOption

interface ItemOptionUseCase {
    fun listByItemId(itemId: Long): List<ItemOption>
    fun getById(id: Long): ItemOption
    fun create(itemId: Long, option: ItemOption): ItemOption
    fun update(id: Long, option: ItemOption): ItemOption
    fun delete(id: Long)
}
