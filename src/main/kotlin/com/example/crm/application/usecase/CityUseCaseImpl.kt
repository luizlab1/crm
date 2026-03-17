package com.example.crm.application.usecase

import com.example.crm.application.port.input.CityUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.City
import com.example.crm.domain.repository.CityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CityUseCaseImpl(
    private val cityRepository: CityRepository
) : CityUseCase {

    override fun list(pageable: Pageable): Page<City> =
        cityRepository.findAll(pageable)

    override fun getById(id: Long): City =
        cityRepository.findById(id) ?: throw EntityNotFoundException("City", id)

    override fun findByStateId(stateId: Long): List<City> =
        cityRepository.findByStateId(stateId)
}

