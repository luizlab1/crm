package com.example.crm.application.port.input

import com.example.crm.domain.model.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PermissionUseCase {
    fun list(pageable: Pageable): Page<Permission>
    fun getById(id: Long): Permission
    fun create(permission: Permission): Permission
    fun update(id: Long, permission: Permission): Permission
    fun delete(id: Long)
}

