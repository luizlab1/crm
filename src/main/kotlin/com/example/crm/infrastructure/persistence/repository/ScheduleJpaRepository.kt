package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.ScheduleJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleJpaRepository : JpaRepository<ScheduleJpaEntity, Long> {
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ScheduleJpaEntity>
    fun findByAppointmentId(appointmentId: Long): List<ScheduleJpaEntity>
}

