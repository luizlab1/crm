package com.example.crm.application.usecase

import com.example.crm.application.port.input.CountryUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Country
import com.example.crm.domain.repository.CountryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CountryUseCaseImpl(
    private val countryRepository: CountryRepository
) : CountryUseCase {

    override fun list(pageable: Pageable): Page<Country> =
        countryRepository.findAll(pageable)

    override fun getById(id: Long): Country =
        countryRepository.findById(id) ?: throw EntityNotFoundException("Country", id)
}

