package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.Customer
import com.example.crm.domain.repository.CustomerRepository
import com.example.crm.infrastructure.persistence.mapper.CustomerPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.CustomerJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class CustomerRepositoryAdapter(
    private val jpaRepository: CustomerJpaRepository,
    private val mapper: CustomerPersistenceMapper
) : CustomerRepository {

    override fun findAll(pageable: Pageable): Page<Customer> =
        jpaRepository.findAll(pageable).map { mapper.toDomain(it) }

    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Customer> =
        jpaRepository.findByTenantId(tenantId, pageable).map { mapper.toDomain(it) }

    override fun findById(id: Long): Customer? =
        jpaRepository.findById(id).map { mapper.toDomain(it) }.orElse(null)

    override fun save(customer: Customer): Customer =
        mapper.toDomain(jpaRepository.save(mapper.toEntity(customer)))
}

