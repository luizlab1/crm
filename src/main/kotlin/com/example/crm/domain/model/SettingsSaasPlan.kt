package com.example.crm.domain.model

import java.time.OffsetDateTime

data class SettingsSaasPlan(
    val id: Long = 0,
    val tenantId: Long,
    val name: String,
    val description: String? = null,
    val category: PlanCategory,
    val benefits: List<SettingsSaasPlanBenefit> = emptyList(),
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

data class SettingsSaasPlanBenefit(
    val id: Long = 0,
    val description: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
