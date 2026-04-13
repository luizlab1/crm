package com.example.crm.application.usecase

import com.example.crm.application.port.input.CustomerUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Customer
import com.example.crm.domain.model.Person
import com.example.crm.domain.repository.CustomerRepository
import com.example.crm.domain.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerUseCaseImpl(
    private val customerRepository: CustomerRepository,
    private val personRepository: PersonRepository
) : CustomerUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Customer> =
        if (tenantId != null) customerRepository.findByTenantId(tenantId, pageable)
        else customerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Customer =
        customerRepository.findById(id) ?: throw EntityNotFoundException("Customer", id)

    override fun create(customer: Customer): Customer {
        val personId = upsertPerson(null, customer.person, customer.tenantId)
        return customerRepository.save(customer.copy(personId = personId ?: customer.personId))
    }

    override fun update(id: Long, customer: Customer): Customer {
        val existing = customerRepository.findById(id) ?: throw EntityNotFoundException("Customer", id)
        val personId = upsertPerson(existing.personId, customer.person, customer.tenantId)
        val updated = customer.copy(
            id = existing.id,
            code = existing.code,
            createdAt = existing.createdAt,
            personId = personId ?: existing.personId
        )
        return customerRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = customerRepository.findById(id) ?: throw EntityNotFoundException("Customer", id)
        customerRepository.save(existing.copy(isActive = false))
    }

    private fun upsertPerson(existingPersonId: Long?, personData: Person?, tenantId: Long): Long? {
        if (personData == null) return existingPersonId
        return if (existingPersonId != null && existingPersonId != 0L) {
            val existing = personRepository.findById(existingPersonId)
            if (existing != null) {
                personRepository.save(
                    personData.copy(
                        id = existing.id,
                        code = existing.code,
                        tenantId = tenantId,
                        createdAt = existing.createdAt
                    )
                ).id
            } else {
                personRepository.save(personData.copy(tenantId = tenantId)).id
            }
        } else {
            personRepository.save(personData.copy(tenantId = tenantId)).id
        }
    }
}
