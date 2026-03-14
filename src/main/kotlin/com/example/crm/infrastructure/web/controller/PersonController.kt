package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.persistence.entity.*
import com.example.crm.infrastructure.persistence.repository.PersonJpaRepository
import com.example.crm.application.service.PersonService
import com.example.crm.infrastructure.web.dto.request.PersonRequest
import com.example.crm.infrastructure.web.dto.response.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/persons")
class PersonController(private val repository: PersonJpaRepository,
                       private val personService: PersonService) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<PersonResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id"))
        val result = personService.list(pageable, tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content, page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PersonResponse> {
        val entity = repository.findById(id).orElseThrow { NoSuchElementException("Person not found: $id") }
        return ResponseEntity.ok(entity.toResponse())
    }

    @PostMapping
    fun create(@RequestBody request: PersonRequest): ResponseEntity<PersonResponse> {
        val person = PersonJpaEntity(tenantId = request.tenantId, isActive = request.isActive)

        request.physical?.let {
            val phys = PersonPhysicalJpaEntity(
                fullName = it.fullName, cpf = it.cpf, birthDate = it.birthDate
            )
            phys.person = person
            person.physical = phys
        }

        request.legal?.let {
            val leg = PersonLegalJpaEntity(
                corporateName = it.corporateName, tradeName = it.tradeName, cnpj = it.cnpj
            )
            leg.person = person
            person.legal = leg
        }

        request.contacts.forEach { c ->
            person.contacts.add(ContactJpaEntity(
                personId = 0, type = c.type, contactValue = c.contactValue,
                isPrimary = c.isPrimary, isActive = c.isActive
            ))
        }

        val saved = repository.save(person)
        // update personId on contacts
        saved.contacts.forEach { it.personId = saved.id }
        val final = repository.save(saved)
        return ResponseEntity.created(URI.create("/api/v1/persons/${final.id}")).body(final.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PersonRequest): ResponseEntity<PersonResponse> {
        val person = repository.findById(id).orElseThrow { NoSuchElementException("Person not found: $id") }
        person.tenantId = request.tenantId
        person.isActive = request.isActive

        // Update physical
        if (request.physical != null) {
            val phys = person.physical ?: PersonPhysicalJpaEntity().also {
                it.person = person
                person.physical = it
            }
            phys.fullName = request.physical.fullName
            phys.cpf = request.physical.cpf
            phys.birthDate = request.physical.birthDate
        } else {
            person.physical = null
        }

        // Update legal
        if (request.legal != null) {
            val leg = person.legal ?: PersonLegalJpaEntity().also {
                it.person = person
                person.legal = it
            }
            leg.corporateName = request.legal.corporateName
            leg.tradeName = request.legal.tradeName
            leg.cnpj = request.legal.cnpj
        } else {
            person.legal = null
        }

        // Replace contacts
        person.contacts.clear()
        request.contacts.forEach { c ->
            person.contacts.add(ContactJpaEntity(
                personId = person.id, type = c.type, contactValue = c.contactValue,
                isPrimary = c.isPrimary, isActive = c.isActive
            ))
        }

        val saved = repository.save(person)
        return ResponseEntity.ok(saved.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val person = repository.findById(id).orElseThrow { NoSuchElementException("Person not found: $id") }
        person.isActive = false
        repository.save(person)
        return ResponseEntity.noContent().build()
    }

    private fun PersonJpaEntity.toResponse() = PersonResponse(
        id = id, tenantId = tenantId, code = code, isActive = isActive,
        physical = physical?.let {
            PersonPhysicalResponse(fullName = it.fullName, cpf = it.cpf, birthDate = it.birthDate)
        },
        legal = legal?.let {
            PersonLegalResponse(corporateName = it.corporateName, tradeName = it.tradeName, cnpj = it.cnpj)
        },
        contacts = contacts.map {
            ContactResponse(
                id = it.id, type = it.type, contactValue = it.contactValue,
                isPrimary = it.isPrimary, isActive = it.isActive,
                createdAt = it.createdAt, updatedAt = it.updatedAt
            )
        },
        createdAt = createdAt, updatedAt = updatedAt
    )
}

