package com.example.crm.domain.repository

import com.example.crm.domain.model.Item
import com.example.crm.domain.model.ItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ItemRepository {
    fun findByFilters(
        code: UUID?,
        tenantId: Long?,
        categoryId: Long?,
        type: ItemType?,
        name: String?,
        sku: String?,
        isActive: Boolean?,
        pageable: Pageable
    ): Page<Item>
    fun findAll(pageable: Pageable): Page<Item>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Item>
    fun findById(id: Long): Item?
    fun countByCategoryId(categoryId: Long): Long
    fun save(item: Item): Item
}

