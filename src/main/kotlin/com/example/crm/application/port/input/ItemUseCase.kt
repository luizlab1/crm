package com.example.crm.application.port.input

import com.example.crm.domain.model.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ItemUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<Item>
    fun getById(id: Long): Item
    fun create(item: Item): Item
    fun update(id: Long, item: Item): Item
    fun delete(id: Long)
}

