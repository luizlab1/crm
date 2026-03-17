package com.example.crm.domain.repository

import com.example.crm.domain.model.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PersonRepository {
    fun findAll(pageable: Pageable): Page<Person>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Person>
    fun findById(id: Long): Person?
    fun save(person: Person): Person
}

