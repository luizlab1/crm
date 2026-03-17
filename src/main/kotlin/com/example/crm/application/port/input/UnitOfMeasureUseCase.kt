package com.example.crm.application.port.input

import com.example.crm.domain.model.UnitOfMeasure
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UnitOfMeasureUseCase {
    fun list(pageable: Pageable): Page<UnitOfMeasure>
    fun getById(id: Long): UnitOfMeasure
}

