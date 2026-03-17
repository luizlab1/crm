package com.example.crm.domain.repository

import com.example.crm.domain.model.Country
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CountryRepository {
    fun findAll(pageable: Pageable): Page<Country>
    fun findById(id: Long): Country?
}

