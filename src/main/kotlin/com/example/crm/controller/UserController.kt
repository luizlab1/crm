package com.example.crm.controller

import com.example.crm.dto.request.UserRequest
import com.example.crm.dto.response.*
import com.example.crm.entity.*
import com.example.crm.service.PersonService
import com.example.crm.service.UserService
import com.example.crm.service.UploadService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val service: UserService,
    private val personService: PersonService,
    private val uploadService: UploadService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<UserSummaryResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("email")), tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toSummary() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val (personEntity, contacts, addresses) = request.toPersonData()
        val created = service.create(request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.created(URI.create("/api/v1/users/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val (personEntity, contacts, addresses) = request.toPersonData()
        val updated = service.update(id, request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.ok(updated.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun UserRequest.toEntity() = UserEntity(
        tenantId = tenantId, email = email, passwordHash = passwordHash, isActive = isActive
    )

    private data class PersonData(
        val entity: PersonEntity?,
        val contacts: List<ContactEntity>,
        val addresses: List<com.example.crm.service.PersonAddressRequest>
    )

    private fun UserRequest.toPersonData(): PersonData {
        val hasPersonPayload = physical != null || legal != null || contacts.isNotEmpty() || addresses.isNotEmpty()
        val personEntity = if (hasPersonPayload) buildPersonEntity(tenantId, isActive, physical, legal) else null
        return PersonData(
            entity = personEntity,
            contacts = contacts.map { it.toEntity() },
            addresses = addresses.map { it.toSvcRequest() }
        )
    }

    private fun UserEntity.toSummary(): UserSummaryResponse {
        val person = personId?.let { personService.getById(it) }
        return UserSummaryResponse(
            id = id, tenantId = tenantId, email = email,
            name = person?.physical?.fullName ?: person?.legal?.corporateName,
            isActive = isActive, createdAt = createdAt,
            photo = resolvePhoto(id, FileType.WORKER)
        )
    }

    private fun UserEntity.toResponse(): UserResponse {
        val person = personId?.let { personService.getById(it) }
        val contacts = personId?.let { personService.getContacts(it) } ?: emptyList()
        val addresses = personId?.let { personService.loadPersonAddresses(it) } ?: emptyList()
        return UserResponse(
            id = id, code = code, tenantId = tenantId, personId = personId,
            email = email, isActive = isActive, createdAt = createdAt, updatedAt = updatedAt,
            photo = resolvePhoto(id, FileType.WORKER),
            physical = person?.physical?.toResponse(),
            legal = person?.legal?.toResponse(),
            contacts = contacts.map { it.toResponse() },
            addresses = addresses.map { it.toResponse() }
        )
    }

    private fun resolvePhoto(entityId: Long, fileType: FileType): String? = try {
        uploadService.list(fileType, entityId, 0, 1).firstOrNull()?.id?.let { uploadId ->
            val base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().removeSuffix("/")
            "$base/api/v1/uploads/$uploadId/view"
        }
    } catch (e: Exception) { null }
}
