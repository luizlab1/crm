package com.example.crm.domain.repository

import com.example.crm.domain.model.State
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface StateRepository {
    fun findAll(pageable: Pageable): Page<State>
    fun findById(id: Long): State?
    fun findByCountryId(countryId: Long): List<State>
}

