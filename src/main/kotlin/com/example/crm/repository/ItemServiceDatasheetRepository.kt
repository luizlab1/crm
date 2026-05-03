package com.example.crm.repository

import com.example.crm.entity.ItemServiceDatasheetEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ItemServiceDatasheetRepository : JpaRepository<ItemServiceDatasheetEntity, Long> {
    fun findByItemId(itemId: Long): Optional<ItemServiceDatasheetEntity>
}
