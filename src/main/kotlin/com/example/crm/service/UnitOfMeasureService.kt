package com.example.crm.service

import com.example.crm.entity.UnitOfMeasureEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.UnitOfMeasureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UnitOfMeasureService(
    private val repository: UnitOfMeasureRepository
) {

    fun list(pageable: Pageable): Page<UnitOfMeasureEntity> = repository.findAll(pageable)

    fun getById(id: Long): UnitOfMeasureEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("UnitOfMeasure", id) }
}
