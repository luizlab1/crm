package com.example.crm.application.usecase

import com.example.crm.application.port.input.AddressUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Address
import com.example.crm.domain.repository.AddressRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AddressUseCaseImpl(
    private val addressRepository: AddressRepository
) : AddressUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<Address> =
        addressRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Address =
        addressRepository.findById(id) ?: throw EntityNotFoundException("Address", id)

    override fun create(address: Address): Address =
        addressRepository.save(address)

    override fun update(id: Long, address: Address): Address {
        val existing = addressRepository.findById(id) ?: throw EntityNotFoundException("Address", id)
        val updated = address.copy(id = existing.id, createdAt = existing.createdAt)
        return addressRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = addressRepository.findById(id) ?: throw EntityNotFoundException("Address", id)
        addressRepository.save(existing.copy(isActive = false))
    }
}

