package com.example.crm.repository

import com.example.crm.entity.ItemEntity
import com.example.crm.entity.ItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ItemRepository : JpaRepository<ItemEntity, Long> {
    @Query(
        """
        select i from ItemEntity i
        where (:code is null or i.code = :code)
          and (:tenantId is null or i.tenantId = :tenantId)
          and (:categoryId is null or i.categoryId = :categoryId)
          and (:type is null or i.type = :type)
          and (:name is null or lower(i.name) like :name)
          and (:sku is null or lower(i.sku) like :sku)
          and (:isActive is null or i.isActive = :isActive)
        """
    )
    fun findByFilters(
        @Param("code") code: UUID?,
        @Param("tenantId") tenantId: Long?,
        @Param("categoryId") categoryId: Long?,
        @Param("type") type: ItemType?,
        @Param("name") name: String?,
        @Param("sku") sku: String?,
        @Param("isActive") isActive: Boolean?,
        pageable: Pageable
    ): Page<ItemEntity>

    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ItemEntity>
    fun countByCategoryId(categoryId: Long): Long
}
