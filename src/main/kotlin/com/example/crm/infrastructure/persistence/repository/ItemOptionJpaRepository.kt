package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ItemOptionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemOptionJpaRepository : JpaRepository<ItemOptionJpaEntity, Long> {
    fun findByItemId(itemId: Long): List<ItemOptionJpaEntity>
    fun deleteByItemId(itemId: Long)
}
