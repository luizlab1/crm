package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ContactJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ContactJpaRepository : JpaRepository<ContactJpaEntity, Long> {
    fun findByPersonId(personId: Long): List<ContactJpaEntity>
}

