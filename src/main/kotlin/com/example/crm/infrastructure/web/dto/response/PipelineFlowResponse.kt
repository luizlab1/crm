package com.example.crm.infrastructure.web.dto.response

import java.time.OffsetDateTime

data class PipelineFlowResponse(
    val id: Long,
    val tenantId: Long,
    val code: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val steps: List<PipelineFlowStepResponse>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class PipelineFlowStepResponse(
    val id: Long,
    val stepOrder: Int,
    val code: String,
    val name: String,
    val description: String?,
    val stepType: String,
    val isTerminal: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

