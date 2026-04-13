package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.Customer
import com.example.crm.domain.repository.CustomerRepository
import com.example.crm.infrastructure.persistence.entity.CustomerJpaEntity
import com.example.crm.infrastructure.persistence.mapper.CustomerPersistenceMapper
import com.example.crm.infrastructure.persistence.mapper.PersonPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.CustomerJpaRepository
import com.example.crm.infrastructure.persistence.repository.PersonJpaRepository
import com.example.crm.infrastructure.persistence.repository.ContactJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class CustomerRepositoryAdapter(
    private val jpaRepository: CustomerJpaRepository,
    private val personJpaRepository: PersonJpaRepository,
    private val contactJpaRepository: ContactJpaRepository,
    private val mapper: CustomerPersistenceMapper,
    private val personMapper: PersonPersistenceMapper
) : CustomerRepository {

    override fun findAll(pageable: Pageable): Page<Customer> {
        val page = jpaRepository.findAll(pageable)
        return page.map { enrich(it) }
    }

    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Customer> {
        val page = jpaRepository.findByTenantId(tenantId, pageable)
        return page.map { enrich(it) }
    }

    override fun findById(id: Long): Customer? =
        jpaRepository.findById(id).map { enrich(it) }.orElse(null)

    override fun save(customer: Customer): Customer =
        mapper.toDomain(jpaRepository.save(mapper.toEntity(customer)))

    private fun enrich(entity: CustomerJpaEntity): Customer {
        val base = mapper.toDomain(entity)
        val person = entity.personId?.let { pid ->
            personJpaRepository.findById(pid).map { personEntity ->
                val contacts = contactJpaRepository.findByPersonIdIn(listOf(pid))
                val domain = personMapper.toDomain(personEntity)
                domain.copy(contacts = contacts.map { personMapper.toDomain(it) })
            }.orElse(null)
        }
        return base.copy(person = person)
    }
}

