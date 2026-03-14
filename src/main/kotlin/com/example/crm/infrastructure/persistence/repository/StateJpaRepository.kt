package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.StateJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StateJpaRepository : JpaRepository<StateJpaEntity, Long> {
    fun findByCountryId(countryId: Long): List<StateJpaEntity>
}

