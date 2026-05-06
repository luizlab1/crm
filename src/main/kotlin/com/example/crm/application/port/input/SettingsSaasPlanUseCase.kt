package com.example.crm.application.port.input

import com.example.crm.domain.model.PlanCategory
import com.example.crm.domain.model.SettingsSaasPlan

data class SettingsSaasPlanBenefitInput(
    val description: String?
)

data class SettingsSaasPlanUpsertInput(
    val tenantId: Long? = null,
    val name: String?,
    val description: String? = null,
    val subtitle: String?,
    val value: String?,
    val category: PlanCategory?,
    val benefits: List<SettingsSaasPlanBenefitInput> = emptyList()
)

interface SettingsSaasPlanUseCase {
    fun list(tenantId: Long, name: String?, category: PlanCategory?): List<SettingsSaasPlan>
    fun getById(id: Long, tenantId: Long): SettingsSaasPlan
    fun create(tenantId: Long, input: SettingsSaasPlanUpsertInput): SettingsSaasPlan
    fun update(id: Long, tenantId: Long, input: SettingsSaasPlanUpsertInput): SettingsSaasPlan
    fun delete(id: Long, tenantId: Long)
}
