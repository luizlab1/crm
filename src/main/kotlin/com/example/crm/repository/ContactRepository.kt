package com.example.crm.repository

import com.example.crm.entity.ContactEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ContactRepository : JpaRepository<ContactEntity, Long> {
    fun findByPersonId(personId: Long): List<ContactEntity>

    @Query("select c from ContactEntity c where c.personId in :personIds")
    fun findByPersonIdIn(@Param("personIds") personIds: List<Long>): List<ContactEntity>
}
