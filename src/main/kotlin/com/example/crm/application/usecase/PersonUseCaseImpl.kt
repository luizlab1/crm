package com.example.crm.application.usecase

import com.example.crm.application.port.input.PersonUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Person
import com.example.crm.domain.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PersonUseCaseImpl(
    private val personRepository: PersonRepository
) : PersonUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Person> =
        if (tenantId != null) personRepository.findByTenantId(tenantId, pageable)
        else personRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Person =
        personRepository.findById(id) ?: throw EntityNotFoundException("Person", id)

    override fun create(person: Person): Person =
        personRepository.save(person)

    override fun update(id: Long, person: Person): Person {
        val existing = personRepository.findById(id) ?: throw EntityNotFoundException("Person", id)
        val updated = person.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return personRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = personRepository.findById(id) ?: throw EntityNotFoundException("Person", id)
        personRepository.save(existing.copy(isActive = false))
    }
}

