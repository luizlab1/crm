package com.example.crm.application.usecase

import com.example.crm.application.port.input.UserUseCase
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.Person
import com.example.crm.domain.model.User
import com.example.crm.domain.repository.PersonRepository
import com.example.crm.domain.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserUseCaseImpl(
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository
) : UserUseCase {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, tenantId: Long?): Page<User> =
        if (tenantId != null) userRepository.findByTenantId(tenantId, pageable)
        else userRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun getById(id: Long): User =
        userRepository.findById(id) ?: throw EntityNotFoundException("User", id)

    @Transactional(readOnly = true)
    override fun getByEmail(email: String): User? = userRepository.findByEmail(email)

    override fun create(user: User): User {
        val personId = upsertPerson(null, user.person, user.tenantId)
        return userRepository.save(user.copy(personId = personId ?: user.personId))
    }

    override fun update(id: Long, user: User): User {
        val existing = userRepository.findById(id) ?: throw EntityNotFoundException("User", id)
        val personId = upsertPerson(existing.personId, user.person, user.tenantId)
        val updated = user.copy(
            id = existing.id,
            code = existing.code,
            createdAt = existing.createdAt,
            personId = personId ?: existing.personId
        )
        return userRepository.save(updated)
    }

    override fun delete(id: Long) {
        val existing = userRepository.findById(id) ?: throw EntityNotFoundException("User", id)
        userRepository.save(existing.copy(isActive = false))
    }

    private fun upsertPerson(existingPersonId: Long?, personData: Person?, tenantId: Long): Long? {
        if (personData == null) return existingPersonId
        return if (existingPersonId != null && existingPersonId != 0L) {
            val existing = personRepository.findById(existingPersonId)
            if (existing != null) {
                personRepository.save(
                    personData.copy(
                        id = existing.id,
                        code = existing.code,
                        tenantId = tenantId,
                        createdAt = existing.createdAt
                    )
                ).id
            } else {
                personRepository.save(personData.copy(tenantId = tenantId)).id
            }
        } else {
            personRepository.save(personData.copy(tenantId = tenantId)).id
        }
    }
}
