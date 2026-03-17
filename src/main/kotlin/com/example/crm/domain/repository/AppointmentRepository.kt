package com.example.crm.domain.repository

import com.example.crm.domain.model.Appointment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AppointmentRepository {
    fun findAll(pageable: Pageable): Page<Appointment>
    fun findById(id: Long): Appointment?
    fun save(appointment: Appointment): Appointment
    fun deleteById(id: Long)
}

