package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ContactJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ContactJpaRepository : JpaRepository<ContactJpaEntity, Long> {
    fun findByPersonId(personId: Long): List<ContactJpaEntity>

    @Query("select c from ContactJpaEntity c where c.personId in :personIds")
    fun findByPersonIdIn(@Param("personIds") personIds: List<Long>): List<ContactJpaEntity>
}

