package com.example.crm.service

import com.example.crm.entity.CustomerEntity
import com.example.crm.entity.PersonEntity
import com.example.crm.entity.ContactEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.CustomerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val personService: PersonService
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<CustomerEntity> =
        if (tenantId != null) customerRepository.findByTenantId(tenantId, pageable)
        else customerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): CustomerEntity =
        customerRepository.findById(id).orElseThrow { EntityNotFoundException("Customer", id) }

    fun create(
        customer: CustomerEntity,
        personData: PersonEntity?,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): CustomerEntity {
        val personId = personData?.let {
            personService.upsertPerson(null, it, customer.tenantId, contacts)
        }
        personId?.let { pid ->
            if (addressRequests.isNotEmpty()) personService.replaceAddresses(pid, addressRequests)
        }
        customer.personId = personId
        return customerRepository.save(customer)
    }

    fun update(
        id: Long,
        customer: CustomerEntity,
        personData: PersonEntity?,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): CustomerEntity {
        val existing = getById(id)
        val personId = personData?.let {
            personService.upsertPerson(existing.personId, it, customer.tenantId, contacts)
        } ?: existing.personId
        personId?.let { pid ->
            if (addressRequests.isNotEmpty()) personService.replaceAddresses(pid, addressRequests)
        }
        existing.fullName = customer.fullName
        existing.email = customer.email
        existing.phone = customer.phone
        existing.document = customer.document
        existing.isActive = customer.isActive
        existing.personId = personId
        return customerRepository.save(existing)
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        customerRepository.save(existing)
    }
}
