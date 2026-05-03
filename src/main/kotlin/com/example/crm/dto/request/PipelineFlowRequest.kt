package com.example.crm.dto.request

data class PipelineFlowRequest(
    val tenantId: Long,
    val code: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val steps: List<PipelineFlowStepRequest> = emptyList()
)

data class PipelineFlowStepRequest(
    val stepOrder: Int,
    val code: String,
    val name: String,
    val description: String? = null,
    val stepType: String,
    val isTerminal: Boolean = false
)
