package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.TenantJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TenantJpaRepository : JpaRepository<TenantJpaEntity, Long>

