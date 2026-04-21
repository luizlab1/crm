package com.example.crm.application.port.input

import com.example.crm.domain.model.ItemCategory
import com.example.crm.domain.model.ItemCategoryPatch
import com.example.crm.domain.model.ItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ItemCategoryUseCase {
    fun list(
        pageable: Pageable,
        tenantId: Long? = null,
        name: String? = null,
        availableTypes: Set<ItemType>? = null,
        showOnSite: Boolean? = null,
        active: Boolean? = null
    ): Page<ItemCategory>
    fun getById(id: Long): ItemCategory
    fun create(itemCategory: ItemCategory): ItemCategory
    fun update(id: Long, itemCategory: ItemCategory): ItemCategory
    fun patch(id: Long, patch: ItemCategoryPatch): ItemCategory
    fun updateSortOrders(sortOrders: Map<Long, Int>): List<ItemCategory>
    fun delete(id: Long)
}

