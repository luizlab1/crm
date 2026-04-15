package com.example.crm.application.usecase

import com.example.crm.application.port.input.TenantUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Person
import com.example.crm.domain.model.Tenant
import com.example.crm.domain.repository.PersonAddressRepository
import com.example.crm.domain.repository.PersonRepository
import com.example.crm.domain.repository.TenantRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TenantUseCaseImpl(
    private val tenantRepository: TenantRepository,
    private val personRepository: PersonRepository,
    private val personAddressRepository: PersonAddressRepository
) : TenantUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<Tenant> =
        tenantRepository.findAll(pageable).map { enrichTenant(it) }

    @Transactional(readOnly = true)
    override fun getById(id: Long): Tenant =
        enrichTenant(tenantRepository.findById(id) ?: throw EntityNotFoundException("Tenant", id))

    override fun create(tenant: Tenant): Tenant {
        val savedTenant = tenantRepository.save(tenant)
        val personId = upsertTenantPerson(savedTenant.id, null, tenant.person)
        if (personId != null && tenant.addresses.isNotEmpty()) {
            personAddressRepository.replaceAddresses(personId, tenant.addresses)
        }
        return enrichTenant(savedTenant)
    }

    override fun update(id: Long, tenant: Tenant): Tenant {
        val existing = tenantRepository.findById(id) ?: throw EntityNotFoundException("Tenant", id)
        val existingPersonId = findTenantProfilePerson(existing.id)?.id
        val personId = upsertTenantPerson(existing.id, existingPersonId, tenant.person)
        if (personId != null && tenant.addresses.isNotEmpty()) {
            personAddressRepository.replaceAddresses(personId, tenant.addresses)
        }

        val updated = tenant.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return enrichTenant(tenantRepository.save(updated))
    }

    override fun delete(id: Long) {
        val existing = tenantRepository.findById(id) ?: throw EntityNotFoundException("Tenant", id)
        tenantRepository.save(existing.copy(isActive = false))
    }

    private fun enrichTenant(tenant: Tenant): Tenant {
        val profilePerson = findTenantProfilePerson(tenant.id)
        val addresses = profilePerson?.let { personAddressRepository.findAddressesByPersonId(it.id) } ?: emptyList()
        return tenant.copy(person = profilePerson, addresses = addresses)
    }

    private fun findTenantProfilePerson(tenantId: Long): Person? {
        val page = personRepository.findByTenantId(
            tenantId,
            PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"))
        )
        return page.content.firstOrNull()
    }

    private fun upsertTenantPerson(tenantId: Long, existingPersonId: Long?, personData: Person?): Long? {
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

