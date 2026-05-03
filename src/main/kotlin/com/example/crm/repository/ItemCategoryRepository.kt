package com.example.crm.repository

import com.example.crm.entity.ItemCategoryEntity
import com.example.crm.entity.ItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemCategoryRepository : JpaRepository<ItemCategoryEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ItemCategoryEntity>

    @Query(
        """
        select i from ItemCategoryEntity i
        where (:tenantId is null or i.tenantId = :tenantId)
          and (:name is null or lower(i.name) like :name)
          and (:showOnSite is null or i.showOnSite = :showOnSite)
          and (:active is null or i.active = :active)
        """
    )
    fun findByFilters(
        @Param("tenantId") tenantId: Long?,
        @Param("name") name: String?,
        @Param("showOnSite") showOnSite: Boolean?,
        @Param("active") active: Boolean?,
        pageable: Pageable
    ): Page<ItemCategoryEntity>
}
