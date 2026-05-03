package com.example.crm.controller

import com.example.crm.dto.request.SettingsSaasPlanRequest
import com.example.crm.dto.response.SettingsSaasPlanBenefitResponse
import com.example.crm.dto.response.SettingsSaasPlanResponse
import com.example.crm.entity.PlanCategory
import com.example.crm.entity.SettingsSaasPlanEntity
import com.example.crm.service.SettingsSaasPlanService
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
@RequestMapping("/api/v1/settings/saas/plans")
class SettingsSaasPlanController(
    private val service: SettingsSaasPlanService
) {

    @GetMapping
    fun findAll(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) category: PlanCategory?,
        authentication: Authentication
    ): ResponseEntity<List<SettingsSaasPlanResponse>> {
        val tenantId = resolveTenantId(authentication)
        val plans = service.list(tenantId, name, category)
        return ResponseEntity.ok(plans.map { it.toResponse() })
    }

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<SettingsSaasPlanResponse> {
        val tenantId = resolveTenantId(authentication)
        val plan = service.getById(id, tenantId)
        return ResponseEntity.ok(plan.toResponse())
    }

    @PostMapping
    fun create(
        @RequestBody request: SettingsSaasPlanRequest,
        authentication: Authentication
    ): ResponseEntity<SettingsSaasPlanResponse> {
        val tenantId = resolveTenantId(authentication)
        val created = service.create(
            tenantId = tenantId,
            name = request.name,
            description = request.description,
            category = request.category,
            benefits = request.benefits.map { it.subtitle to it.value }
        )
        return ResponseEntity.created(URI.create("/api/v1/settings/saas/plans/${created.id}"))
            .body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: SettingsSaasPlanRequest,
        authentication: Authentication
    ): ResponseEntity<SettingsSaasPlanResponse> {
        val tenantId = resolveTenantId(authentication)
        val updated = service.update(
            id = id,
            tenantId = tenantId,
            name = request.name,
            description = request.description,
            category = request.category,
            benefits = request.benefits.map { it.subtitle to it.value }
        )
        return ResponseEntity.ok(updated.toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val tenantId = resolveTenantId(authentication)
        service.delete(id, tenantId)
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

    private fun SettingsSaasPlanEntity.toResponse() = SettingsSaasPlanResponse(
        id = id,
        tenantId = tenantId,
        name = name,
        description = description,
        category = category,
        benefits = benefits.map { benefit ->
            SettingsSaasPlanBenefitResponse(
                id = benefit.id,
                subtitle = benefit.subtitle,
                value = benefit.value
            )
        },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
