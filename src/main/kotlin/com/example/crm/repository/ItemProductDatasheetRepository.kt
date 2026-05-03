package com.example.crm.repository

import com.example.crm.entity.ItemProductDatasheetEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ItemProductDatasheetRepository : JpaRepository<ItemProductDatasheetEntity, Long> {
    fun findByItemId(itemId: Long): Optional<ItemProductDatasheetEntity>
}
