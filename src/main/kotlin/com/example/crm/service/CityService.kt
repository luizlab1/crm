package com.example.crm.service

import com.example.crm.entity.CityEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.CityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CityService(
    private val repository: CityRepository
) {

    fun list(pageable: Pageable): Page<CityEntity> = repository.findAll(pageable)

    fun getById(id: Long): CityEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("City", id) }

    fun findByStateId(stateId: Long): List<CityEntity> = repository.findByStateId(stateId)
}
