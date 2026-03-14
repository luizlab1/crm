package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.RoleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoleJpaRepository : JpaRepository<RoleJpaEntity, Long>

