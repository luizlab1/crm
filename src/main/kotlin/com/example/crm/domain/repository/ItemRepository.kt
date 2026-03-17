package com.example.crm.domain.repository

import com.example.crm.domain.model.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ItemRepository {
    fun findAll(pageable: Pageable): Page<Item>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Item>
    fun findById(id: Long): Item?
    fun save(item: Item): Item
}

