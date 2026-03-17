package com.example.crm.domain.repository

import com.example.crm.domain.model.UnitOfMeasure
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UnitOfMeasureRepository {
    fun findAll(pageable: Pageable): Page<UnitOfMeasure>
    fun findById(id: Long): UnitOfMeasure?
}

