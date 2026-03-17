package com.example.crm.application.usecase

import com.example.crm.application.port.input.WorkerUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Worker
import com.example.crm.domain.repository.WorkerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkerUseCaseImpl(
    private val workerRepository: WorkerRepository
) : WorkerUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Worker> =
        if (tenantId != null) workerRepository.findByTenantId(tenantId, pageable)
        else workerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Worker =
        workerRepository.findById(id) ?: throw EntityNotFoundException("Worker", id)

    override fun create(worker: Worker): Worker =
        workerRepository.save(worker)

    override fun update(id: Long, worker: Worker): Worker {
        val existing = workerRepository.findById(id) ?: throw EntityNotFoundException("Worker", id)
        val updated = worker.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return workerRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = workerRepository.findById(id) ?: throw EntityNotFoundException("Worker", id)
        workerRepository.save(existing.copy(isActive = false))
    }
}

