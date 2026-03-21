package com.example.crm.application.port.input

import com.example.crm.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserUseCase {
    fun list(pageable: Pageable, tenantId: Long?): Page<User>
    fun getById(id: Long): User
    fun getByEmail(email: String): User?
    fun create(user: User): User
    fun update(id: Long, user: User): User
    fun delete(id: Long)
}
