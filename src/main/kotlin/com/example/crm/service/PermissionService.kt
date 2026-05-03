package com.example.crm.service

import com.example.crm.entity.PermissionEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.PermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PermissionService(
    private val repository: PermissionRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<PermissionEntity> = repository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): PermissionEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("Permission", id) }

    fun create(entity: PermissionEntity): PermissionEntity = repository.save(entity)

    fun update(id: Long, entity: PermissionEntity): PermissionEntity {
        val existing = getById(id)
        existing.code = entity.code
        existing.description = entity.description
        existing.isActive = entity.isActive
        return repository.save(existing)
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        repository.save(existing)
    }
}
