package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ItemImageJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemImageJpaRepository : JpaRepository<ItemImageJpaEntity, Long> {
    fun findByItemIdOrderBySortOrder(itemId: Long): List<ItemImageJpaEntity>
    fun deleteByItemId(itemId: Long)
}
