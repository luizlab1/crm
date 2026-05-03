package com.example.crm.repository

import com.example.crm.entity.PlanCategory
import com.example.crm.entity.SettingsSaasPlanJpaEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SettingsSaasPlanJpaRepository : JpaRepository<SettingsSaasPlanJpaEntity, Long> {

    @Query(
        """
        select distinct p from SettingsSaasPlanJpaEntity p
        left join fetch p.benefits b
        where p.tenantId = :tenantId
          and (:name is null or lower(p.name) like :name)
          and (:category is null or p.category = :category)
        order by p.id desc
        """
    )
    fun findByTenantIdAndFilters(
        @Param("tenantId") tenantId: Long,
        @Param("name") name: String?,
        @Param("category") category: PlanCategory?
    ): List<SettingsSaasPlanJpaEntity>

    @EntityGraph(attributePaths = ["benefits"])
    fun findOneByIdAndTenantId(id: Long, tenantId: Long): SettingsSaasPlanJpaEntity?
}
