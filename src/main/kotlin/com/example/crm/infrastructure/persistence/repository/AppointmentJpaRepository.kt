package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.AppointmentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentJpaRepository : JpaRepository<AppointmentJpaEntity, Long>

