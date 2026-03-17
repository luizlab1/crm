package com.example.crm.domain.repository

import com.example.crm.domain.model.City
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CityRepository {
    fun findAll(pageable: Pageable): Page<City>
    fun findById(id: Long): City?
    fun findByStateId(stateId: Long): List<City>
}

