package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.SettingsSaasPlanUseCase
import com.example.crm.domain.model.PlanCategory
import com.example.crm.infrastructure.web.dto.request.SettingsSaasPlanRequest
import com.example.crm.infrastructure.web.dto.response.SettingsSaasPlanResponse
import com.example.crm.infrastructure.web.mapper.SettingsSaasPlanWebMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping(value = ["/api/v1/settings/saas/plans", "/settings/saas/plans"])
class SettingsSaasPlanController(
    private val useCase: SettingsSaasPlanUseCase,
    private val mapper: SettingsSaasPlanWebMapper
) {

    @GetMapping
    fun findAll(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) category: PlanCategory?,
        authentication: Authentication
    ): ResponseEntity<List<SettingsSaasPlanResponse>> {
        val tenantId = resolveTenantId(authentication)
        val plans = useCase.list(tenantId, name, category)
        return ResponseEntity.ok(plans.map(mapper::toResponse))
    }

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<SettingsSaasPlanResponse> {
        val tenantId = resolveTenantId(authentication)
        val plan = useCase.getById(id, tenantId)
        return ResponseEntity.ok(mapper.toResponse(plan))
    }

    @PostMapping
    fun create(
        @RequestBody request: SettingsSaasPlanRequest,
        authentication: Authentication
    ): ResponseEntity<SettingsSaasPlanResponse> {
        val tenantId = resolveTenantId(authentication)
        val created = useCase.create(tenantId, mapper.toUpsertInput(request))
        return ResponseEntity.created(URI.create("/api/v1/settings/saas/plans/${created.id}"))
            .body(mapper.toResponse(created))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: SettingsSaasPlanRequest,
        authentication: Authentication
    ): ResponseEntity<SettingsSaasPlanResponse> {
        val tenantId = resolveTenantId(authentication)
        val updated = useCase.update(id, tenantId, mapper.toUpsertInput(request))
        return ResponseEntity.ok(mapper.toResponse(updated))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val tenantId = resolveTenantId(authentication)
        useCase.delete(id, tenantId)
        return ResponseEntity.noContent().build()
    }

    private fun resolveTenantId(authentication: Authentication): Long {
        val details = authentication.details
        require(details is Map<*, *>) { "Token invalido: tenantId ausente" }
        val tenantClaim = details["tenantId"]
        return when (tenantClaim) {
            is Number -> tenantClaim.toLong()
            is String -> tenantClaim.toLongOrNull() ?: error("Token invalido: tenantId ausente")
            else -> error("Token invalido: tenantId ausente")
        }
    }
}
