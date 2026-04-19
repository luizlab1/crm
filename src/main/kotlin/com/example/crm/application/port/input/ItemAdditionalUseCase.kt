package com.example.crm.application.port.input

import com.example.crm.domain.model.ItemAdditional

interface ItemAdditionalUseCase {
    fun listByItemId(itemId: Long): List<ItemAdditional>
    fun getById(id: Long): ItemAdditional
    fun create(itemId: Long, additional: ItemAdditional): ItemAdditional
    fun update(id: Long, additional: ItemAdditional): ItemAdditional
    fun delete(id: Long)
}
