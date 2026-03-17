package com.example.crm.domain.repository

import com.example.crm.domain.model.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RoleRepository {
    fun findAll(pageable: Pageable): Page<Role>
    fun findById(id: Long): Role?
    fun save(role: Role): Role
}

