package com.example.crm.service

import com.example.crm.entity.ContactEntity
import com.example.crm.entity.PersonEntity
import com.example.crm.entity.WorkerEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.WorkerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkerService(
    private val workerRepository: WorkerRepository,
    private val personService: PersonService
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<WorkerEntity> =
        if (tenantId != null) workerRepository.findByTenantId(tenantId, pageable)
        else workerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): WorkerEntity =
        workerRepository.findById(id).orElseThrow { EntityNotFoundException("Worker", id) }

    fun create(
        worker: WorkerEntity,
        personData: PersonEntity,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): WorkerEntity {
        val personId = personService.upsertPerson(null, personData, worker.tenantId, contacts)
        if (addressRequests.isNotEmpty()) personService.replaceAddresses(personId, addressRequests)
        worker.personId = personId
        return workerRepository.save(worker)
    }

    fun update(
        id: Long,
        worker: WorkerEntity,
        personData: PersonEntity,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): WorkerEntity {
        val existing = getById(id)
        val personId = personService.upsertPerson(existing.personId, personData, worker.tenantId, contacts)
        if (addressRequests.isNotEmpty()) personService.replaceAddresses(personId, addressRequests)
        existing.tenantId = worker.tenantId
        existing.userId = worker.userId
        existing.isActive = worker.isActive
        existing.personId = personId
        return workerRepository.save(existing)
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        workerRepository.save(existing)
    }
}
