package com.example.crm.dto.request

data class PermissionRequest(
    val code: String,
    val description: String? = null,
    val isActive: Boolean = true
)
