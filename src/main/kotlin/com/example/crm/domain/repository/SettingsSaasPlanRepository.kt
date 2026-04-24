package com.example.crm.domain.repository

import com.example.crm.domain.model.PlanCategory
import com.example.crm.domain.model.SettingsSaasPlan

interface SettingsSaasPlanRepository {
    fun findByTenantIdAndFilters(tenantId: Long, name: String?, category: PlanCategory?): List<SettingsSaasPlan>
    fun findByIdAndTenantId(id: Long, tenantId: Long): SettingsSaasPlan?
    fun save(plan: SettingsSaasPlan): SettingsSaasPlan
    fun deleteById(id: Long)
}
