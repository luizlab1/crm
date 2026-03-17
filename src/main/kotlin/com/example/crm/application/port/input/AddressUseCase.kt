package com.example.crm.application.port.input

import com.example.crm.domain.model.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AddressUseCase {
    fun list(pageable: Pageable): Page<Address>
    fun getById(id: Long): Address
    fun create(address: Address): Address
    fun update(id: Long, address: Address): Address
    fun delete(id: Long)
}

