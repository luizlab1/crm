package com.example.crm.application.port.input

import com.example.crm.domain.model.Schedule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ScheduleUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<Schedule>
    fun getById(id: Long): Schedule
    fun create(schedule: Schedule): Schedule
    fun update(id: Long, schedule: Schedule): Schedule
    fun delete(id: Long)
}

