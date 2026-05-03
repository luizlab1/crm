package com.example.crm.service

import com.example.crm.entity.AddressEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.AddressRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AddressService(
    private val repository: AddressRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<AddressEntity> = repository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): AddressEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("Address", id) }

    fun create(entity: AddressEntity): AddressEntity = repository.save(entity)

    fun update(id: Long, entity: AddressEntity): AddressEntity {
        val existing = getById(id)
        existing.street = entity.street
        existing.number = entity.number
        existing.complement = entity.complement
        existing.neighborhood = entity.neighborhood
        existing.cityId = entity.cityId
        existing.postalCode = entity.postalCode
        existing.latitude = entity.latitude
        existing.longitude = entity.longitude
        existing.isActive = entity.isActive
        return repository.save(existing)
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        repository.save(existing)
    }
}
