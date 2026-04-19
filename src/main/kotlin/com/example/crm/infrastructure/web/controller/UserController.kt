package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.UserUseCase
import com.example.crm.infrastructure.web.dto.request.UserRequest
import com.example.crm.infrastructure.web.dto.response.PageResponse
import com.example.crm.infrastructure.web.dto.response.UserResponse
import com.example.crm.infrastructure.web.dto.response.UserSummaryResponse
import com.example.crm.infrastructure.web.mapper.EntityPhotoResolver
import com.example.crm.infrastructure.web.mapper.UserWebMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val useCase: UserUseCase,
    private val mapper: UserWebMapper,
    private val photoResolver: EntityPhotoResolver
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) tenantId: Long?
    ): ResponseEntity<PageResponse<UserSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("id"))
        val result = useCase.list(pageable, tenantId)
        return ResponseEntity.ok(PageResponse(
            content = result.content.map {
                mapper.toSummary(it).copy(photo = photoResolver.resolve(it.id))
            },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)).copy(photo = photoResolver.resolve(id)))

    @PostMapping
    fun create(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val created = useCase.create(mapper.toDomain(request))
        return ResponseEntity.created(URI.create("/api/v1/users/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserRequest): ResponseEntity<UserResponse> =
        ResponseEntity.ok(mapper.toResponse(useCase.update(id, mapper.toDomain(request))))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        useCase.delete(id)
        return ResponseEntity.noContent().build()
    }
}
