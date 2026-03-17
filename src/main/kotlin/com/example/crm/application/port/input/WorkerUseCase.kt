package com.example.crm.application.port.input

import com.example.crm.domain.model.Worker
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WorkerUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<Worker>
    fun getById(id: Long): Worker
    fun create(worker: Worker): Worker
    fun update(id: Long, worker: Worker): Worker
    fun delete(id: Long)
}

