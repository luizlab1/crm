package com.example.crm.application.port.input

import com.example.crm.domain.model.City
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CityUseCase {
    fun list(pageable: Pageable): Page<City>
    fun getById(id: Long): City
    fun findByStateId(stateId: Long): List<City>
}

