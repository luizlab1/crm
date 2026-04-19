package com.example.crm.domain.repository

import com.example.crm.domain.model.ItemAdditional

interface ItemAdditionalRepository {
    fun findByItemId(itemId: Long): List<ItemAdditional>
    fun findById(id: Long): ItemAdditional?
    fun save(additional: ItemAdditional): ItemAdditional
    fun deleteById(id: Long)
}
