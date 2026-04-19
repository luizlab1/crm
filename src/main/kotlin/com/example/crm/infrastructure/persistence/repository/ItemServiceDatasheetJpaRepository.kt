package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ItemServiceDatasheetJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ItemServiceDatasheetJpaRepository : JpaRepository<ItemServiceDatasheetJpaEntity, Long> {
    fun findByItemId(itemId: Long): Optional<ItemServiceDatasheetJpaEntity>
}
