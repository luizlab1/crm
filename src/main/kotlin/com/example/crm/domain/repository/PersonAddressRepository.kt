package com.example.crm.domain.repository

import com.example.crm.domain.model.PersonAddress

interface PersonAddressRepository {
    fun findAddressesByPersonId(personId: Long): List<PersonAddress>
    fun findAddressesByPersonIds(personIds: List<Long>): Map<Long, List<PersonAddress>>
    fun replaceAddresses(personId: Long, addresses: List<PersonAddress>): List<PersonAddress>
}
