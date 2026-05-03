package com.example.crm.infrastructure.web.dto.request

import com.example.crm.domain.model.PlanCategory
import com.fasterxml.jackson.annotation.JsonProperty

data class SettingsSaasPlanRequest(
    @JsonProperty("tenant_id")
    val tenantId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val category: PlanCategory? = null,
    val benefits: List<SettingsSaasPlanBenefitRequest> = emptyList()
)

data class SettingsSaasPlanBenefitRequest(
    val subtitle: String? = null,
    val value: String? = null
)
