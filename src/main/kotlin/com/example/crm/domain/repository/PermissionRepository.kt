package com.example.crm.domain.repository

import com.example.crm.domain.model.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PermissionRepository {
    fun findAll(pageable: Pageable): Page<Permission>
    fun findById(id: Long): Permission?
    fun save(permission: Permission): Permission
}

