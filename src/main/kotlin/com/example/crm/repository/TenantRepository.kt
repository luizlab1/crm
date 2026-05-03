package com.example.crm.repository

import com.example.crm.entity.TenantEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TenantRepository : JpaRepository<TenantEntity, Long>
