package com.example.crm.application.port.input

import com.example.crm.domain.model.Country
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CountryUseCase {
    fun list(pageable: Pageable): Page<Country>
    fun getById(id: Long): Country
}

