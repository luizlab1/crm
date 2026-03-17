package com.example.crm.domain.repository

import com.example.crm.domain.model.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AddressRepository {
    fun findAll(pageable: Pageable): Page<Address>
    fun findById(id: Long): Address?
    fun save(address: Address): Address
}

