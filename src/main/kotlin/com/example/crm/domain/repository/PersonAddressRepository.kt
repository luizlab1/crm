package com.example.crm.domain.repository

import com.example.crm.domain.model.Address

interface PersonAddressRepository {
    fun findPrimaryAddressByPersonId(personId: Long): Address?
    fun findPrimaryAddressesByPersonIds(personIds: List<Long>): Map<Long, Address>
    fun upsertPrimaryAddress(personId: Long, address: Address): Address
}
