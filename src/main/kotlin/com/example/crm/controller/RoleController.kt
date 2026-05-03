package com.example.crm.controller

import com.example.crm.dto.request.RoleRequest
import com.example.crm.dto.response.PageResponse
import com.example.crm.dto.response.RoleResponse
import com.example.crm.entity.RoleEntity
import com.example.crm.service.RoleService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/roles")
class RoleController(
    private val service: RoleService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<RoleResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("name")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<RoleResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: RoleRequest): ResponseEntity<RoleResponse> {
        val created = service.create(RoleEntity(name = request.name, description = request.description, isActive = request.isActive))
        return ResponseEntity.created(URI.create("/api/v1/roles/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: RoleRequest): ResponseEntity<RoleResponse> =
        ResponseEntity.ok(service.update(id, RoleEntity(name = request.name, description = request.description, isActive = request.isActive)).toResponse())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun RoleEntity.toResponse() = RoleResponse(id, name, description, isActive, createdAt, updatedAt)
}
