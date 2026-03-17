package com.example.crm.application.port.input

import com.example.crm.domain.model.ItemCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ItemCategoryUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<ItemCategory>
    fun getById(id: Long): ItemCategory
    fun create(itemCategory: ItemCategory): ItemCategory
    fun update(id: Long, itemCategory: ItemCategory): ItemCategory
    fun delete(id: Long)
}

