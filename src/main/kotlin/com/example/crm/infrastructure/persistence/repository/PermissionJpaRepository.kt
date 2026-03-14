package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.PermissionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionJpaRepository : JpaRepository<PermissionJpaEntity, Long>

