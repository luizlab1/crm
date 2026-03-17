package com.example.crm.domain.repository

import com.example.crm.domain.model.Schedule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ScheduleRepository {
    fun findAll(pageable: Pageable): Page<Schedule>
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Schedule>
    fun findById(id: Long): Schedule?
    fun save(schedule: Schedule): Schedule
}

