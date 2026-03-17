package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.Person
import com.example.crm.domain.repository.PersonRepository
import com.example.crm.infrastructure.persistence.mapper.PersonPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.PersonJpaRepository
import com.example.crm.infrastructure.persistence.repository.ContactJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PersonRepositoryAdapter(
    private val jpaRepository: PersonJpaRepository,
    private val contactJpaRepository: ContactJpaRepository,
    private val mapper: PersonPersistenceMapper
) : PersonRepository {

    override fun findAll(pageable: Pageable): Page<Person> {
        val page = jpaRepository.findAll(pageable)
        val contactsByPerson = loadContactsByPersonIds(page.content.map { it.id })
        return page.map { entity ->
            val domain = mapper.toDomain(entity)
            val contacts = contactsByPerson[entity.id]?.map { mapper.toDomain(it) } ?: emptyList()
            domain.copy(contacts = contacts)
        }
    }

    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Person> {
        val page = jpaRepository.findByTenantId(tenantId, pageable)
        val contactsByPerson = loadContactsByPersonIds(page.content.map { it.id })
        return page.map { entity ->
            val domain = mapper.toDomain(entity)
            val contacts = contactsByPerson[entity.id]?.map { mapper.toDomain(it) } ?: emptyList()
            domain.copy(contacts = contacts)
        }
    }

    override fun findById(id: Long): Person? =
        jpaRepository.findById(id).map { mapper.toDomain(it) }.orElse(null)

    override fun save(person: Person): Person {
        val entity = mapper.toEntity(person)
        val saved = jpaRepository.save(entity)
        // fix child foreign keys after first insert
        if (person.id == 0L) {
            saved.contacts.forEach { it.personId = saved.id }
            val final = jpaRepository.save(saved)
            return mapper.toDomain(final)
        }
        return mapper.toDomain(saved)
    }

    private fun loadContactsByPersonIds(ids: List<Long>) =
        if (ids.isEmpty()) emptyMap()
        else contactJpaRepository.findByPersonIdIn(ids).groupBy { it.personId }
}

