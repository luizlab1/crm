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

    fun update(id: Long, entity: AddressEntity): AddressEntity =
        repository.save(
            getById(id).apply {
                street = entity.street
                number = entity.number
                complement = entity.complement
                neighborhood = entity.neighborhood
                cityId = entity.cityId
                postalCode = entity.postalCode
                latitude = entity.latitude
                longitude = entity.longitude
                isActive = entity.isActive
            }
        )

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        repository.save(existing)
    }
}
