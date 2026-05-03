package com.example.crm.repository

import com.example.crm.entity.AppointmentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentRepository : JpaRepository<AppointmentEntity, Long>
