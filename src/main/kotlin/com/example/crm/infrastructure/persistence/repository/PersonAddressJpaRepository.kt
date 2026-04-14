package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.PersonAddressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PersonAddressJpaRepository : JpaRepository<PersonAddressJpaEntity, Long> {
    fun findByPersonIdOrderByIsPrimaryDescIdAsc(personId: Long): List<PersonAddressJpaEntity>
    fun findByPersonIdIn(personIds: List<Long>): List<PersonAddressJpaEntity>
}
