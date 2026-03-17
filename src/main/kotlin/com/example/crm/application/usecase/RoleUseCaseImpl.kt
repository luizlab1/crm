package com.example.crm.application.usecase

import com.example.crm.application.port.input.RoleUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Role
import com.example.crm.domain.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RoleUseCaseImpl(
    private val roleRepository: RoleRepository
) : RoleUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<Role> =
        roleRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): Role =
        roleRepository.findById(id) ?: throw EntityNotFoundException("Role", id)

    override fun create(role: Role): Role =
        roleRepository.save(role)

    override fun update(id: Long, role: Role): Role {
        val existing = roleRepository.findById(id) ?: throw EntityNotFoundException("Role", id)
        val updated = role.copy(id = existing.id, createdAt = existing.createdAt)
        return roleRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = roleRepository.findById(id) ?: throw EntityNotFoundException("Role", id)
        roleRepository.save(existing.copy(isActive = false))
    }
}

