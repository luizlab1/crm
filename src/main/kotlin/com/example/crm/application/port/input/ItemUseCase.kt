package com.example.crm.application.port.input

import com.example.crm.domain.model.Item
import com.example.crm.domain.model.ItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ItemUseCase {
    fun list(
        pageable: Pageable,
        code: UUID?,
        tenantId: Long?,
        categoryId: Long?,
        type: ItemType?,
        name: String?,
        sku: String?,
        isActive: Boolean?
    ): Page<Item>
    fun getById(id: Long): Item
    fun create(item: Item): Item
    fun update(id: Long, item: Item): Item
    fun delete(id: Long)
}

