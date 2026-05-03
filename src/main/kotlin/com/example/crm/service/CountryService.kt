package com.example.crm.service

import com.example.crm.entity.CountryEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.CountryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CountryService(
    private val repository: CountryRepository
) {

    fun list(pageable: Pageable): Page<CountryEntity> = repository.findAll(pageable)

    fun getById(id: Long): CountryEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("Country", id) }
}
