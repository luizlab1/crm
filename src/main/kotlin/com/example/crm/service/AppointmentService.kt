package com.example.crm.service

import com.example.crm.entity.AppointmentEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.AppointmentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AppointmentService(
    private val repository: AppointmentRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<AppointmentEntity> = repository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): AppointmentEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("Appointment", id) }

    fun create(entity: AppointmentEntity): AppointmentEntity = repository.save(entity)

    fun update(id: Long, entity: AppointmentEntity): AppointmentEntity {
        val existing = getById(id)
        existing.status = entity.status
        existing.scheduledAt = entity.scheduledAt
        existing.startedAt = entity.startedAt
        existing.finishedAt = entity.finishedAt
        existing.totalCents = entity.totalCents
        existing.notes = entity.notes
        return repository.save(existing)
    }

    fun delete(id: Long) {
        getById(id)
        repository.deleteById(id)
    }
}
