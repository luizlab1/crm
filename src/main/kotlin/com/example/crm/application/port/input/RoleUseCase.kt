package com.example.crm.application.port.input

import com.example.crm.domain.model.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RoleUseCase {
    fun list(pageable: Pageable): Page<Role>
    fun getById(id: Long): Role
    fun create(role: Role): Role
    fun update(id: Long, role: Role): Role
    fun delete(id: Long)
}

