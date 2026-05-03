package com.example.crm.service

import com.example.crm.entity.ScheduleEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.ScheduleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScheduleService(
    private val repository: ScheduleRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<ScheduleEntity> =
        if (tenantId != null) repository.findByTenantId(tenantId, pageable)
        else repository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): ScheduleEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("Schedule", id) }

    fun create(entity: ScheduleEntity): ScheduleEntity = repository.save(entity)

    fun update(id: Long, entity: ScheduleEntity): ScheduleEntity =
        repository.save(
            getById(id).apply {
                tenantId = entity.tenantId
                customerId = entity.customerId
                appointmentId = entity.appointmentId
                description = entity.description
                isActive = entity.isActive
            }
        )

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        repository.save(existing)
    }
}
