package com.example.crm.infrastructure.persistence.repository

import com.example.crm.domain.model.ItemType
import com.example.crm.infrastructure.persistence.entity.ItemCategoryJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemCategoryJpaRepository : JpaRepository<ItemCategoryJpaEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ItemCategoryJpaEntity>

    @Query(
        """
        select i from ItemCategoryJpaEntity i
        where (:tenantId is null or i.tenantId = :tenantId)
          and (:name is null or lower(i.name) like :name)
          and (:showOnSite is null or i.showOnSite = :showOnSite)
        """
    )
    fun findByFilters(
        @Param("tenantId") tenantId: Long?,
        @Param("name") name: String?,
        @Param("showOnSite") showOnSite: Boolean?,
        pageable: Pageable
    ): Page<ItemCategoryJpaEntity>
}

