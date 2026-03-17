package com.example.crm.application.port.input

import com.example.crm.domain.model.State
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface StateUseCase {
    fun list(pageable: Pageable): Page<State>
    fun getById(id: Long): State
    fun findByCountryId(countryId: Long): List<State>
}

