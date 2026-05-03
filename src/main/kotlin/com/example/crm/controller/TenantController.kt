package com.example.crm.controller

import com.example.crm.dto.request.TenantRequest
import com.example.crm.dto.response.PageResponse
import com.example.crm.dto.response.TenantResponse
import com.example.crm.dto.response.TenantSummaryResponse
import com.example.crm.entity.ContactEntity
import com.example.crm.entity.FileType
import com.example.crm.entity.PersonEntity
import com.example.crm.entity.TenantEntity
import com.example.crm.service.PersonService
import com.example.crm.service.TenantService
import com.example.crm.service.UploadService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/api/v1/tenants")
class TenantController(
    private val service: TenantService,
    private val personService: PersonService,
    private val uploadService: UploadService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<TenantSummaryResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("name")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toSummary() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<TenantResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: TenantRequest): ResponseEntity<TenantResponse> {
        val (personEntity, contacts, addresses) = request.toPersonData()
        val created = service.create(request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.created(URI.create("/api/v1/tenants/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: TenantRequest): ResponseEntity<TenantResponse> {
        val (personEntity, contacts, addresses) = request.toPersonData()
        val updated = service.update(id, request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.ok(updated.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun TenantRequest.toEntity() = TenantEntity(
        parentTenantId = parentTenantId, name = name, category = category, isActive = isActive
    )

    private data class PersonData(
        val entity: PersonEntity?,
        val contacts: List<ContactEntity>,
        val addresses: List<com.example.crm.service.PersonAddressRequest>
    )

    private fun TenantRequest.toPersonData(): PersonData {
        val hasPersonPayload = physical != null || legal != null || contacts.isNotEmpty() || addresses.isNotEmpty()
        val personEntity = if (hasPersonPayload) buildPersonEntity(0L, isActive, physical, legal) else null
        return PersonData(
            entity = personEntity,
            contacts = contacts.map { it.toEntity() },
            addresses = addresses.map { it.toSvcRequest() }
        )
    }

    private fun TenantEntity.toSummary(): TenantSummaryResponse {
        val person = service.getProfilePerson(id)
        return TenantSummaryResponse(
            id = id, parentTenantId = parentTenantId, name = name, category = category,
            document = person?.physical?.cpf ?: person?.legal?.cnpj,
            isActive = isActive, createdAt = createdAt,
            photo = resolvePhoto(id)
        )
    }

    private fun TenantEntity.toResponse(): TenantResponse {
        val person = service.getProfilePerson(id)
        val contacts = person?.let { personService.getContacts(it.id) } ?: emptyList()
        val addresses = person?.let { personService.loadPersonAddresses(it.id) } ?: emptyList()
        return TenantResponse(
            id = id, parentTenantId = parentTenantId, code = code, name = name,
            category = category, isActive = isActive, createdAt = createdAt, updatedAt = updatedAt,
            photo = resolvePhoto(id),
            physical = person?.physical?.toResponse(),
            legal = person?.legal?.toResponse(),
            contacts = contacts.map { it.toResponse() },
            addresses = addresses.map { it.toResponse() }
        )
    }

    private fun resolvePhoto(entityId: Long): String? = runCatching {
        uploadService.list(FileType.CATEGORY, entityId, 0, 1).firstOrNull()?.id?.let { uploadId ->
            val base = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString()
                .removeSuffix("/")
            "$base/api/v1/uploads/$uploadId/view"
        }
    }.getOrNull()
}
