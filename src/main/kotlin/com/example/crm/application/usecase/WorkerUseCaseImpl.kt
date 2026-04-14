package com.example.crm.application.usecase

import com.example.crm.application.port.input.WorkerUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Person
import com.example.crm.domain.model.Worker
import com.example.crm.domain.repository.PersonAddressRepository
import com.example.crm.domain.repository.PersonRepository
import com.example.crm.domain.repository.WorkerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkerUseCaseImpl(
    private val workerRepository: WorkerRepository,
    private val personRepository: PersonRepository,
    private val personAddressRepository: PersonAddressRepository
) : WorkerUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Worker> =
        if (tenantId != null) workerRepository.findByTenantId(tenantId, pageable)
        else workerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Worker =
        workerRepository.findById(id) ?: throw EntityNotFoundException("Worker", id)

    override fun create(worker: Worker): Worker {
        val personId = upsertPerson(null, worker.person, worker.tenantId)
        val effectivePersonId = personId ?: worker.personId
        if (worker.address != null) {
            personAddressRepository.upsertPrimaryAddress(effectivePersonId, worker.address)
        }

        val saved = workerRepository.save(worker.copy(personId = effectivePersonId))
        return workerRepository.findById(saved.id) ?: saved
    }

    override fun update(id: Long, worker: Worker): Worker {
        val existing = workerRepository.findById(id) ?: throw EntityNotFoundException("Worker", id)
        val personId = upsertPerson(existing.personId, worker.person, worker.tenantId)
        val updated = worker.copy(
            id = existing.id,
            code = existing.code,
            createdAt = existing.createdAt,
            personId = personId ?: existing.personId
        )
        val finalPersonId = updated.personId
        if (worker.address != null) {
            personAddressRepository.upsertPrimaryAddress(finalPersonId, worker.address)
        }

        val saved = workerRepository.save(updated)
        return workerRepository.findById(saved.id) ?: saved
    }

    override fun delete(id: Long) {
        val existing = workerRepository.findById(id) ?: throw EntityNotFoundException("Worker", id)
        workerRepository.save(existing.copy(isActive = false))
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
