package com.example.crm.repository

import com.example.crm.entity.ItemAdditionalEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemAdditionalRepository : JpaRepository<ItemAdditionalEntity, Long> {
    fun findByItemId(itemId: Long): List<ItemAdditionalEntity>
    fun deleteByItemId(itemId: Long)
}
