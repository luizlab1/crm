package com.example.crm.service

import com.example.crm.entity.UserEntity
import com.example.crm.entity.ContactEntity
import com.example.crm.entity.PersonEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val personService: PersonService
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<UserEntity> =
        if (tenantId != null) userRepository.findByTenantId(tenantId, pageable)
        else userRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): UserEntity =
        userRepository.findById(id).orElseThrow { EntityNotFoundException("User", id) }

    @Transactional(readOnly = true)
    fun getByEmail(email: String): UserEntity? = userRepository.findByEmail(email)

    fun create(
        user: UserEntity,
        personData: PersonEntity?,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): UserEntity {
        val personId = personData?.let {
            personService.upsertPerson(null, it, user.tenantId, contacts)
        }
        personId?.let { pid ->
            if (addressRequests.isNotEmpty()) personService.replaceAddresses(pid, addressRequests)
        }
        user.personId = personId
        return userRepository.save(user)
    }

    fun update(
        id: Long,
        user: UserEntity,
        personData: PersonEntity?,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): UserEntity {
        val existing = getById(id)
        val personId = personData?.let {
            personService.upsertPerson(existing.personId, it, user.tenantId, contacts)
        } ?: existing.personId
        personId?.let { pid ->
            if (addressRequests.isNotEmpty()) personService.replaceAddresses(pid, addressRequests)
        }
        existing.email = user.email
        if (!user.passwordHash.isNullOrBlank()) {
            existing.passwordHash = user.passwordHash
        }
        existing.isActive = user.isActive
        existing.personId = personId
        return userRepository.save(existing)
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        userRepository.save(existing)
    }
}
