package com.example.crm.application.usecase

import com.example.crm.application.port.input.StateUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.State
import com.example.crm.domain.repository.StateRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StateUseCaseImpl(
    private val stateRepository: StateRepository
) : StateUseCase {

    override fun list(pageable: Pageable): Page<State> =
        stateRepository.findAll(pageable)

    override fun getById(id: Long): State =
        stateRepository.findById(id) ?: throw EntityNotFoundException("State", id)

    override fun findByCountryId(countryId: Long): List<State> =
        stateRepository.findByCountryId(countryId)
}

