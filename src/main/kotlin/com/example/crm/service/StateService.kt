package com.example.crm.service

import com.example.crm.entity.StateEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.StateRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StateService(
    private val repository: StateRepository
) {

    fun list(pageable: Pageable): Page<StateEntity> = repository.findAll(pageable)

    fun getById(id: Long): StateEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("State", id) }

    fun findByCountryId(countryId: Long): List<StateEntity> = repository.findByCountryId(countryId)
}
