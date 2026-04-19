package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ItemTagJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemTagJpaRepository : JpaRepository<ItemTagJpaEntity, Long> {
    fun findByItemId(itemId: Long): List<ItemTagJpaEntity>
    fun deleteByItemId(itemId: Long)
}
