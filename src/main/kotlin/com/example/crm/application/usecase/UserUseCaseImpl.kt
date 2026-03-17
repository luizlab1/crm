package com.example.crm.application.usecase

import com.example.crm.application.port.input.UserUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.User
import com.example.crm.domain.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserUseCaseImpl(
    private val userRepository: UserRepository
) : UserUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<User> =
        if (tenantId != null) userRepository.findByTenantId(tenantId, pageable)
        else userRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): User =
        userRepository.findById(id) ?: throw EntityNotFoundException("User", id)

    override fun create(user: User): User =
        userRepository.save(user)

    override fun update(id: Long, user: User): User {
        val existing = userRepository.findById(id) ?: throw EntityNotFoundException("User", id)
        val updated = user.copy(id = existing.id, code = existing.code, createdAt = existing.createdAt)
        return userRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = userRepository.findById(id) ?: throw EntityNotFoundException("User", id)
        userRepository.save(existing.copy(isActive = false))
    }
}

