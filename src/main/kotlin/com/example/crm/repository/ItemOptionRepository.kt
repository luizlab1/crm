package com.example.crm.repository

import com.example.crm.entity.ItemOptionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemOptionRepository : JpaRepository<ItemOptionEntity, Long> {
    fun findByItemId(itemId: Long): List<ItemOptionEntity>
    fun deleteByItemId(itemId: Long)
}
