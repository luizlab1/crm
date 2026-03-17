package com.example.crm.domain.model

import java.time.OffsetDateTime

data class PipelineFlow(
    val id: Long = 0,
    val tenantId: Long,
    val code: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val steps: List<PipelineFlowStep> = emptyList(),
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

data class PipelineFlowStep(
    val id: Long = 0,
    val stepOrder: Int,
    val code: String,
    val name: String,
    val description: String? = null,
    val stepType: String,
    val isTerminal: Boolean = false,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

