package com.example.crm.service

import com.example.crm.entity.RoleEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RoleService(
    private val repository: RoleRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<RoleEntity> = repository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): RoleEntity =
        repository.findById(id).orElseThrow { EntityNotFoundException("Role", id) }

    fun create(entity: RoleEntity): RoleEntity = repository.save(entity)

    fun update(id: Long, entity: RoleEntity): RoleEntity =
        repository.save(
            getById(id).apply {
                name = entity.name
                description = entity.description
                isActive = entity.isActive
            }
        )

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        repository.save(existing)
    }
}
