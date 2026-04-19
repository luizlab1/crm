package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ItemAdditionalJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemAdditionalJpaRepository : JpaRepository<ItemAdditionalJpaEntity, Long> {
    fun findByItemId(itemId: Long): List<ItemAdditionalJpaEntity>
    fun deleteByItemId(itemId: Long)
}
