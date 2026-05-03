package com.example.crm.dto.request

data class RoleRequest(
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true
)
