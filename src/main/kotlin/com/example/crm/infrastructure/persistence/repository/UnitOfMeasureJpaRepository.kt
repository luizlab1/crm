package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.UnitOfMeasureJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UnitOfMeasureJpaRepository : JpaRepository<UnitOfMeasureJpaEntity, Long>

