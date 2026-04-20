package com.example.crm.domain.repository

import com.example.crm.domain.model.ItemCategory
import com.example.crm.domain.model.ItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ItemCategoryRepository {
    fun findAll(pageable: Pageable): Page<ItemCategory>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ItemCategory>
    fun findByFilters(
        tenantId: Long?,
        name: String?,
        availableTypes: Set<ItemType>?,
        showOnSite: Boolean?,
        pageable: Pageable
    ): Page<ItemCategory>
    fun findById(id: Long): ItemCategory?
    fun save(itemCategory: ItemCategory): ItemCategory
    fun deleteById(id: Long)
}

