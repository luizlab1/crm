package com.example.crm.repository

import com.example.crm.entity.ScheduleEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleRepository : JpaRepository<ScheduleEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ScheduleEntity>
    fun findByAppointmentId(appointmentId: Long): List<ScheduleEntity>
}
