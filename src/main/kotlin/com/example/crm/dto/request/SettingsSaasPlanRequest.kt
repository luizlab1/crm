package com.example.crm.dto.request

import com.example.crm.entity.PlanCategory
import com.fasterxml.jackson.annotation.JsonProperty

data class SettingsSaasPlanRequest(
    @JsonProperty("tenant_id")
    val tenantId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val category: PlanCategory? = null,
    val subtitle: String? = null,
    val value: String? = null,
    val benefits: List<SettingsSaasPlanBenefitRequest> = emptyList()
)

data class SettingsSaasPlanBenefitRequest(
    val description: String? = null
)
