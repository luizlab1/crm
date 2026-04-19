package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ItemProductDatasheetJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ItemProductDatasheetJpaRepository : JpaRepository<ItemProductDatasheetJpaEntity, Long> {
    fun findByItemId(itemId: Long): Optional<ItemProductDatasheetJpaEntity>
}
