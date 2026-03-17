package com.example.crm.application.usecase

import com.example.crm.application.port.input.AppointmentUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Appointment
import com.example.crm.domain.repository.AppointmentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AppointmentUseCaseImpl(
    private val appointmentRepository: AppointmentRepository
) : AppointmentUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<Appointment> =
        appointmentRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Appointment =
        appointmentRepository.findById(id) ?: throw EntityNotFoundException("Appointment", id)

    override fun create(appointment: Appointment): Appointment =
        appointmentRepository.save(appointment)

    override fun update(id: Long, appointment: Appointment): Appointment {
        val existing = appointmentRepository.findById(id) ?: throw EntityNotFoundException("Appointment", id)
        val updated = appointment.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return appointmentRepository.save(updated)
    }

    override fun delete(id: Long) {
        appointmentRepository.findById(id) ?: throw EntityNotFoundException("Appointment", id)
        appointmentRepository.deleteById(id)
    }
}

