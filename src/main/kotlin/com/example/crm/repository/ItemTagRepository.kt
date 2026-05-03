package com.example.crm.repository

import com.example.crm.entity.ItemTagEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemTagRepository : JpaRepository<ItemTagEntity, Long> {
    fun findByItemId(itemId: Long): List<ItemTagEntity>
    fun deleteByItemId(itemId: Long)
}
