package com.example.crm.controller

import com.example.crm.dto.request.WorkerRequest
import com.example.crm.dto.response.*
import com.example.crm.entity.*
import com.example.crm.service.PersonService
import com.example.crm.service.WorkerService
import com.example.crm.service.UploadService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/api/v1/workers")
class WorkerController(
    private val service: WorkerService,
    private val personService: PersonService,
    private val uploadService: UploadService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<WorkerSummaryResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("id")), tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toSummary() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<WorkerResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: WorkerRequest): ResponseEntity<WorkerResponse> {
        val personEntity = buildPersonEntity(request.tenantId, request.isActive, request.physical, request.legal)
        val contacts = request.contacts.map { it.toEntity() }
        val addresses = request.addresses.map { it.toSvcRequest() }
        val created = service.create(request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.created(URI.create("/api/v1/workers/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: WorkerRequest): ResponseEntity<WorkerResponse> {
        val personEntity = buildPersonEntity(request.tenantId, request.isActive, request.physical, request.legal)
        val contacts = request.contacts.map { it.toEntity() }
        val addresses = request.addresses.map { it.toSvcRequest() }
        val updated = service.update(id, request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.ok(updated.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun WorkerRequest.toEntity() = WorkerEntity(
        tenantId = tenantId, userId = userId, isActive = isActive
    )

    private fun WorkerEntity.toSummary(): WorkerSummaryResponse {
        val person = personService.getById(personId)
        return WorkerSummaryResponse(
            id = id, tenantId = tenantId,
            name = person.physical?.fullName ?: person.legal?.corporateName,
            document = person.physical?.cpf ?: person.legal?.cnpj,
            isActive = isActive, createdAt = createdAt,
            photo = resolvePhoto(id, FileType.WORKER)
        )
    }

    private fun WorkerEntity.toResponse(): WorkerResponse {
        val person = personService.getById(personId)
        val contacts = personService.getContacts(personId)
        val addresses = personService.loadPersonAddresses(personId)
        return WorkerResponse(
            id = id, code = code, tenantId = tenantId, personId = personId,
            userId = userId, isActive = isActive, createdAt = createdAt, updatedAt = updatedAt,
            photo = resolvePhoto(id, FileType.WORKER),
            physical = person.physical?.toResponse(),
            legal = person.legal?.toResponse(),
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
