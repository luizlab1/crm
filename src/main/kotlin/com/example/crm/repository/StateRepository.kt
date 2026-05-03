package com.example.crm.repository

import com.example.crm.entity.StateEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StateRepository : JpaRepository<StateEntity, Long> {
    fun findByCountryId(countryId: Long): List<StateEntity>
}
