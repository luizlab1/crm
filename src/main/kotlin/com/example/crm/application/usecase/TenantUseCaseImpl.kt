package com.example.crm.application.usecase

import com.example.crm.application.port.input.TenantUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Tenant
import com.example.crm.domain.repository.TenantRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TenantUseCaseImpl(
    private val tenantRepository: TenantRepository
) : TenantUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<Tenant> =
        tenantRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Tenant =
        tenantRepository.findById(id) ?: throw EntityNotFoundException("Tenant", id)

    override fun create(tenant: Tenant): Tenant =
        tenantRepository.save(tenant)

    override fun update(id: Long, tenant: Tenant): Tenant {
        val existing = tenantRepository.findById(id) ?: throw EntityNotFoundException("Tenant", id)
        val updated = tenant.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return tenantRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = tenantRepository.findById(id) ?: throw EntityNotFoundException("Tenant", id)
        tenantRepository.save(existing.copy(isActive = false))
    }
}

