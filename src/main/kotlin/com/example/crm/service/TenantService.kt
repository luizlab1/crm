package com.example.crm.service

import com.example.crm.entity.*
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TenantService(
    private val tenantRepository: TenantRepository,
    private val personService: PersonService
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<TenantEntity> = tenantRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): TenantEntity =
        tenantRepository.findById(id).orElseThrow { EntityNotFoundException("Tenant", id) }

    @Transactional(readOnly = true)
    fun getProfilePerson(tenantId: Long): PersonEntity? {
        val page = personService.list(
            PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id")),
            tenantId
        )
        return page.content.firstOrNull()
    }

    fun create(
        entity: TenantEntity,
        personData: PersonEntity?,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): TenantEntity {
        val saved = tenantRepository.save(entity)
        personData?.let { pd ->
            val personId = personService.upsertPerson(null, pd, saved.id, contacts)
            if (addressRequests.isNotEmpty()) personService.replaceAddresses(personId, addressRequests)
        }
        return saved
    }

    fun update(
        id: Long,
        entity: TenantEntity,
        personData: PersonEntity?,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): TenantEntity {
        val existing = getById(id)
        existing.parentTenantId = entity.parentTenantId
        existing.name = entity.name
        existing.category = entity.category
        existing.isActive = entity.isActive
        val saved = tenantRepository.save(existing)
        personData?.let { pd ->
            val existingPersonId = getProfilePerson(saved.id)?.id
            val personId = personService.upsertPerson(existingPersonId, pd, saved.id, contacts)
            if (addressRequests.isNotEmpty()) personService.replaceAddresses(personId, addressRequests)
        }
        return saved
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        tenantRepository.save(existing)
    }
}
