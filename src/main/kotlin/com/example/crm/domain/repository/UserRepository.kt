package com.example.crm.domain.repository

import com.example.crm.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepository {
    fun findAll(pageable: Pageable): Page<User>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<User>
    fun findById(id: Long): User?
    fun save(user: User): User
    fun findByEmail(email: String): User?
}
