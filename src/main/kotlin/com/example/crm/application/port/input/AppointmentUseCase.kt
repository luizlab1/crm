package com.example.crm.application.port.input

import com.example.crm.domain.model.Appointment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AppointmentUseCase {
    fun list(pageable: Pageable): Page<Appointment>
    fun getById(id: Long): Appointment
    fun create(appointment: Appointment): Appointment
    fun update(id: Long, appointment: Appointment): Appointment
    fun delete(id: Long)
}

