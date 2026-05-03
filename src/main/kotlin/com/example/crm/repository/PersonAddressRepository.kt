package com.example.crm.repository

import com.example.crm.entity.PersonAddressEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PersonAddressRepository : JpaRepository<PersonAddressEntity, Long> {
    fun findByPersonIdOrderByIsPrimaryDescIdAsc(personId: Long): List<PersonAddressEntity>
    fun findByPersonIdIn(personIds: List<Long>): List<PersonAddressEntity>
}
