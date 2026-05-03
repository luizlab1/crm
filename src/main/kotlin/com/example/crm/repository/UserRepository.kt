package com.example.crm.repository

import com.example.crm.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<UserEntity>
    fun findByEmail(email: String): UserEntity?
}
