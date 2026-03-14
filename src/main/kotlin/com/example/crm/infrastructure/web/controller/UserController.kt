package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.UserJpaEntity
import com.example.crm.infrastructure.persistence.repository.UserJpaRepository
import com.example.crm.infrastructure.web.dto.request.UserRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.UserResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val repository: UserJpaRepository) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<UserResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id"))
        val result = if (tenantId != null) repository.findByTenantId(tenantId, pageable)
                     else repository.findAll(pageable)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("User not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val entity = UserJpaEntity(
            tenantId = request.tenantId, personId = request.personId,
            email = request.email, passwordHash = request.passwordHash,
            isActive = request.isActive
        )
        val saved = repository.save(entity)
        return ResponseEntity.created(URI.create("/api/v1/users/${saved.id}")).body(saved.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("User not found: $id") }
        entity.tenantId = request.tenantId
        entity.personId = request.personId
        entity.email = request.email
        entity.passwordHash = request.passwordHash
        entity.isActive = request.isActive
        val saved = repository.save(entity)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("User not found: $id") }
        entity.isActive = false
        repository.save(entity)
        return ResponseEntity.noContent().build()
    }

    private fun UserJpaEntity.toResponse() = UserResponse(
        id = id, tenantId = tenantId, personId = personId, code = code,
        email = email, isActive = isActive,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

