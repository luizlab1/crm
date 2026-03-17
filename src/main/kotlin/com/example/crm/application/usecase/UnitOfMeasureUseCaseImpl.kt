package com.example.crm.application.usecase

import com.example.crm.application.port.input.UnitOfMeasureUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.UnitOfMeasure
import com.example.crm.domain.repository.UnitOfMeasureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UnitOfMeasureUseCaseImpl(
    private val unitOfMeasureRepository: UnitOfMeasureRepository
) : UnitOfMeasureUseCase {

    override fun list(pageable: Pageable): Page<UnitOfMeasure> =
        unitOfMeasureRepository.findAll(pageable)

    override fun getById(id: Long): UnitOfMeasure =
        unitOfMeasureRepository.findById(id) ?: throw EntityNotFoundException("UnitOfMeasure", id)
}

