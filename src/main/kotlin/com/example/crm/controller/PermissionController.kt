package com.example.crm.controller

import com.example.crm.dto.request.PermissionRequest
import com.example.crm.dto.response.PageResponse
import com.example.crm.dto.response.PermissionResponse
import com.example.crm.entity.PermissionEntity
import com.example.crm.service.PermissionService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/permissions")
class PermissionController(
    private val service: PermissionService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<PermissionResponse>> {
        val result = service.list(PageRequest.of(page, size, Sort.by("code")))
        return ResponseEntity.ok(PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number, size = result.size,
            totalElements = result.totalElements, totalPages = result.totalPages
        ))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PermissionResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @PostMapping
    fun create(@RequestBody request: PermissionRequest): ResponseEntity<PermissionResponse> {
        val created = service.create(PermissionEntity(code = request.code, description = request.description, isActive = request.isActive))
        return ResponseEntity.created(URI.create("/api/v1/permissions/${created.id}")).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: PermissionRequest): ResponseEntity<PermissionResponse> =
        ResponseEntity.ok(service.update(id, PermissionEntity(code = request.code, description = request.description, isActive = request.isActive)).toResponse())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun PermissionEntity.toResponse() = PermissionResponse(id, code, description, isActive, createdAt, updatedAt)
}
