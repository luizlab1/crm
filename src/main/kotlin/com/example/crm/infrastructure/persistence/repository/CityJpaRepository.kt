package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.CityJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CityJpaRepository : JpaRepository<CityJpaEntity, Long> {
    fun findByStateId(stateId: Long): List<CityJpaEntity>
}

