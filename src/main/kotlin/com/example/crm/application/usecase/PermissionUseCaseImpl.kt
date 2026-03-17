package com.example.crm.application.usecase

import com.example.crm.application.port.input.PermissionUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Permission
import com.example.crm.domain.repository.PermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PermissionUseCaseImpl(
    private val permissionRepository: PermissionRepository
) : PermissionUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<Permission> =
        permissionRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Permission =
        permissionRepository.findById(id) ?: throw EntityNotFoundException("Permission", id)

    override fun create(permission: Permission): Permission =
        permissionRepository.save(permission)

    override fun update(id: Long, permission: Permission): Permission {
        val existing = permissionRepository.findById(id) ?: throw EntityNotFoundException("Permission", id)
        val updated = permission.copy(id = existing.id, createdAt = existing.createdAt)
        return permissionRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = permissionRepository.findById(id) ?: throw EntityNotFoundException("Permission", id)
        permissionRepository.save(existing.copy(isActive = false))
    }
}

