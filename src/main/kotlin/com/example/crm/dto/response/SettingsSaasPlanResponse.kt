package com.example.crm.dto.response

import com.example.crm.entity.PlanCategory
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class SettingsSaasPlanResponse(
    val id: Long,
    @JsonProperty("tenant_id")
    val tenantId: Long,
    val name: String,
    val description: String?,
    val category: PlanCategory,
    val subtitle: String,
    val value: String,
    val benefits: List<SettingsSaasPlanBenefitResponse>,
    @JsonProperty("created_at")
    val createdAt: OffsetDateTime,
    @JsonProperty("updated_at")
    val updatedAt: OffsetDateTime
)

data class SettingsSaasPlanBenefitResponse(
    val id: Long,
    val description: String
)
