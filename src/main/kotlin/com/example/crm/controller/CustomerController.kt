package com.example.crm.controller

import com.example.crm.dto.request.CustomerRequest
import com.example.crm.dto.request.PersonAddressRequest as WebPersonAddressRequest
import com.example.crm.dto.response.CustomerResponse
import com.example.crm.dto.response.CustomerSummaryResponse
import com.example.crm.dto.response.PageResponse
import com.example.crm.dto.response.PersonAddressResponse
import com.example.crm.dto.response.PersonPhysicalResponse
import com.example.crm.dto.response.PersonLegalResponse
import com.example.crm.dto.response.ContactResponse
import com.example.crm.entity.CustomerEntity
import com.example.crm.entity.PersonEntity
import com.example.crm.entity.ContactEntity
import com.example.crm.entity.PersonPhysicalEntity
import com.example.crm.entity.PersonLegalEntity
import com.example.crm.entity.FileType
import com.example.crm.service.PersonAddressWithAddress
import com.example.crm.service.CustomerService
import com.example.crm.service.PersonAddressRequest as SvcPersonAddressRequest
import com.example.crm.service.PersonAddressType as SvcPersonAddressType
import com.example.crm.service.UploadService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController(
    private val service: CustomerService,
    private val uploadService: UploadService,
    private val personService: com.example.crm.service.PersonService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<CustomerSummaryResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("fullName")), tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toSummary() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerResponse> {
        val c = service.getById(id)
        return ResponseEntity.ok(c.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse> {
        val (personEntity, contacts, addresses) = request.toPersonData()
        val created = service.create(request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.created(URI.create("/api/v1/customers/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse> {
        val (personEntity, contacts, addresses) = request.toPersonData()
        val updated = service.update(id, request.toEntity(), personEntity, contacts, addresses)
        return ResponseEntity.ok(updated.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun CustomerRequest.toEntity() = CustomerEntity(
        tenantId = tenantId, fullName = fullName,
        email = email, phone = phone, document = document, isActive = isActive
    )

    private data class PersonData(
        val entity: PersonEntity?,
        val contacts: List<ContactEntity>,
        val addresses: List<SvcPersonAddressRequest>
    )

    private fun CustomerRequest.toPersonData(): PersonData {
        val hasPersonPayload = physical != null || legal != null || contacts.isNotEmpty() || addresses.isNotEmpty()
        val personEntity = if (hasPersonPayload) buildPersonEntity(tenantId, isActive, physical, legal) else null
        return PersonData(
            entity = personEntity,
            contacts = contacts.map { it.toEntity() },
            addresses = addresses.map { it.toSvcRequest() }
        )
    }

    private fun CustomerEntity.toSummary() = CustomerSummaryResponse(
        id = id, tenantId = tenantId, fullName = fullName,
        email = email, phone = phone, document = document,
        isActive = isActive, createdAt = createdAt,
        photo = resolvePhoto(id, FileType.CUSTOMER)
    )

    private fun CustomerEntity.toResponse(): CustomerResponse {
        val addresses = personId?.let { personService.loadPersonAddresses(it) } ?: emptyList()
        val contacts = personId?.let { personService.getContacts(it) } ?: emptyList()
        val person = personId?.let { personService.getById(it) }
        return CustomerResponse(
            id = id,
            code = code,
            tenantId = tenantId,
            personId = personId,
            fullName = fullName,
            email = email,
            phone = phone,
            document = document,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt,
            photo = resolvePhoto(id, FileType.CUSTOMER),
            physical = person?.physical?.toResponse(),
            legal = person?.legal?.toResponse(),
            contacts = contacts.map { it.toResponse() },
            addresses = addresses.map { it.toResponse() }
        )
    }

    private fun resolvePhoto(entityId: Long, fileType: FileType): String? = runCatching {
        val uploads = uploadService.list(fileType, entityId, 0, 1)
        uploads.firstOrNull()?.id?.let { uploadId ->
            val base = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString()
                .removeSuffix("/")
            "$base/api/v1/uploads/$uploadId/view"
        }
    }.getOrNull()
}

// Shared extension helpers used across person-related controllers
internal fun buildPersonEntity(
    tenantId: Long,
    isActive: Boolean,
    physical: com.example.crm.dto.request.PersonPhysicalRequest?,
    legal: com.example.crm.dto.request.PersonLegalRequest?
): PersonEntity {
    val person = PersonEntity(tenantId = tenantId, isActive = isActive)
    physical?.let { ph ->
        person.physical = PersonPhysicalEntity(fullName = ph.fullName, cpf = ph.cpf, birthDate = ph.birthDate)
            .also { it.person = person }
    }
    legal?.let { lg ->
        person.legal = PersonLegalEntity(corporateName = lg.corporateName, tradeName = lg.tradeName, cnpj = lg.cnpj)
            .also { it.person = person }
    }
    return person
}

internal fun com.example.crm.dto.request.ContactRequest.toEntity() = ContactEntity(
    personId = 0L, type = type, contactValue = contactValue, isPrimary = isPrimary, isActive = isActive
)

internal fun WebPersonAddressRequest.toSvcRequest() = SvcPersonAddressRequest(
    id = id,
    type = SvcPersonAddressType.valueOf(type.name),
    isPrimary = isPrimary, street = street, number = number, complement = complement,
    neighborhood = neighborhood, cityId = cityId, postalCode = postalCode,
    latitude = latitude, longitude = longitude, isActive = isActive
)

internal fun PersonPhysicalEntity.toResponse() = PersonPhysicalResponse(
    fullName = fullName, cpf = cpf, birthDate = birthDate
)

internal fun PersonLegalEntity.toResponse() = PersonLegalResponse(
    corporateName = corporateName, tradeName = tradeName, cnpj = cnpj
)

internal fun ContactEntity.toResponse() = ContactResponse(
    id = id, type = type, contactValue = contactValue,
    isPrimary = isPrimary, isActive = isActive, createdAt = createdAt, updatedAt = updatedAt
)

internal fun com.example.crm.service.PersonAddressWithAddress.toResponse() = PersonAddressResponse(
    id = link.id,
    type = com.example.crm.dto.request.PersonAddressType.valueOf(link.type),
    isPrimary = link.isPrimary,
    street = address.street, number = address.number, complement = address.complement,
    neighborhood = address.neighborhood, cityId = address.cityId, postalCode = address.postalCode,
    latitude = address.latitude, longitude = address.longitude, isActive = address.isActive,
    createdAt = address.createdAt, updatedAt = address.updatedAt
)
