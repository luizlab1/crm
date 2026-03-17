package com.example.crm.application.usecase

import com.example.crm.application.port.input.ScheduleUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Schedule
import com.example.crm.domain.repository.ScheduleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : ScheduleUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<Schedule> =
        if (tenantId != null) scheduleRepository.findByTenantId(tenantId, pageable)
        else scheduleRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Schedule =
        scheduleRepository.findById(id) ?: throw EntityNotFoundException("Schedule", id)

    override fun create(schedule: Schedule): Schedule =
        scheduleRepository.save(schedule)

    override fun update(id: Long, schedule: Schedule): Schedule {
        val existing = scheduleRepository.findById(id) ?: throw EntityNotFoundException("Schedule", id)
        val updated = schedule.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return scheduleRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = scheduleRepository.findById(id) ?: throw EntityNotFoundException("Schedule", id)
        scheduleRepository.save(existing.copy(isActive = false))
    }
}

