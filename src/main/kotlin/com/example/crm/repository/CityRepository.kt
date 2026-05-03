package com.example.crm.repository

import com.example.crm.entity.CityEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CityRepository : JpaRepository<CityEntity, Long> {
    fun findByStateId(stateId: Long): List<CityEntity>
}
