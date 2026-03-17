package com.example.crm.application.port.input

import com.example.crm.domain.model.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PersonUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<Person>
    fun getById(id: Long): Person
    fun create(person: Person): Person
    fun update(id: Long, person: Person): Person
    fun delete(id: Long)
}

