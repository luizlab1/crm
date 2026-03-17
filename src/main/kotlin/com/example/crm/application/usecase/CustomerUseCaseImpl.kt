package com.example.crm.application.usecase

import com.example.crm.application.port.input.CustomerUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Customer
import com.example.crm.domain.repository.CustomerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerUseCaseImpl(
    private val customerRepository: CustomerRepository
) : CustomerUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Customer> =
        if (tenantId != null) customerRepository.findByTenantId(tenantId, pageable)
        else customerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Customer =
        customerRepository.findById(id) ?: throw EntityNotFoundException("Customer", id)

    override fun create(customer: Customer): Customer =
        customerRepository.save(customer)

    override fun update(id: Long, customer: Customer): Customer {
        val existing = customerRepository.findById(id) ?: throw EntityNotFoundException("Customer", id)
        val updated = customer.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return customerRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = customerRepository.findById(id) ?: throw EntityNotFoundException("Customer", id)
        customerRepository.save(existing.copy(isActive = false))
    }
}

