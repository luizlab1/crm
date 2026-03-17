package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.PersonUseCase
import com.example.crm.infrastructure.web.dto.request.PersonRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.PersonResponse
import com.example.crm.infrastructure.web.mapper.PersonWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/persons")
class PersonController(
    private val useCase: PersonUseCase,
    private val mapper: PersonWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<PersonResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id"))
        val result = useCase.list(pageable, tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { mapper.toResponse(it) },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PersonResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @PostMapping
    fun create(@RequestBody request: PersonRequest): ResponseEntity<PersonResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/persons/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PersonRequest): ResponseEntity<PersonResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
