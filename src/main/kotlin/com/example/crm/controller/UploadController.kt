package com.example.crm.controller

import com.example.crm.dto.request.UploadPatchRequest
import com.example.crm.dto.response.FileTypeRuleResponse
import com.example.crm.dto.response.UploadResponse
import com.example.crm.dto.response.UploadRulesResponse
import com.example.crm.entity.FileType
import com.example.crm.entity.UploadEntity
import com.example.crm.infrastructure.config.UploadFileResourceResolver
import com.example.crm.service.UploadService
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/uploads")
class UploadController(
    private val service: UploadService,
    private val fileResolver: UploadFileResourceResolver
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("fileType") fileType: FileType,
        @RequestParam("entityId") entityId: Long,
        @RequestParam(defaultValue = "0") sortOrder: Int,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) subtitle: String?,
        @RequestParam(required = false) width: Int?,
        @RequestParam(required = false) height: Int?,
        @RequestParam(required = false) quality: Int?,
        authentication: Authentication
    ): ResponseEntity<UploadResponse> {
        require(!file.isEmpty) { "Arquivo obrigatório" }
        val tenantId = resolveTenantId(authentication)
        val created = service.upload(
            content = file.bytes,
            originalFileName = file.originalFilename,
            contentType = file.contentType,
            fileType = fileType,
            tenantId = tenantId,
            entityId = entityId,
            width = width, height = height,
            sortOrder = sortOrder,
            title = title, subtitle = subtitle,
            quality = quality
        )
        return ResponseEntity.created(URI.create("/api/v1/uploads/${created.id}"))
            .body(withLinks(created.toResponse()))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) fileType: FileType?,
        @RequestParam(required = false) entityId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<UploadResponse>> =
        ResponseEntity.ok(service.list(fileType, entityId, page, size).map { withLinks(it.toResponse()) })

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<UploadResponse> =
        ResponseEntity.ok(withLinks(service.getById(id).toResponse()))

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UploadPatchRequest
    ): ResponseEntity<UploadResponse> {
        val updated = service.update(
            id,
            request.fileType,
            request.entityId,
            request.sortOrder,
            request.title,
            request.subtitle
        )
        return ResponseEntity.ok(withLinks(updated.toResponse()))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/view")
    fun view(@PathVariable id: UUID): ResponseEntity<Resource> {
        val upload = service.getById(id)
        val resource = fileResolver.resolveResource(upload.filePath)
        return ResponseEntity.ok()
            .contentType(fileResolver.resolveMediaType(upload.contentType))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.inline().filename(upload.fileName).build().toString()
            )
            .contentLength(resource.contentLength())
            .body(resource)
    }

    @GetMapping("/{id}/download")
    fun download(@PathVariable id: UUID): ResponseEntity<Resource> {
        val upload = service.getById(id)
        val resource = fileResolver.resolveResource(upload.filePath)
        return ResponseEntity.ok()
            .contentType(fileResolver.resolveMediaType(upload.contentType))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(upload.fileName).build().toString()
            )
            .contentLength(resource.contentLength())
            .body(resource)
    }

    @GetMapping("/rules")
    fun rules(): ResponseEntity<UploadRulesResponse> {
        val settings = service.getRules()
        val perType = settings.allRules().map { (fileType, rule) ->
            FileTypeRuleResponse(
                fileType = fileType,
                displayName = fileType.displayName,
                allowedExtensions = rule.allowedExtensions,
                maxSizeBytes = rule.maxSizeBytes,
                maxWidth = rule.maxWidth,
                maxHeight = rule.maxHeight
            )
        }
        return ResponseEntity.ok(UploadRulesResponse(
            minQuality = settings.minQuality,
            maxQuality = settings.maxQuality,
            rules = perType
        ))
    }

    private fun UploadEntity.toResponse() = UploadResponse(
        id = id,
        fileType = fileType,
        entityId = entityId,
        tenantId = tenantId,
        itemId = itemId,
        categoryId = categoryId,
        customerId = customerId,
        workerId = workerId,
        fileName = fileName,
        filePath = filePath,
        contentType = contentType,
        size = size,
        width = width,
        height = height,
        sortOrder = sortOrder,
        title = title,
        subtitle = subtitle,
        createdAt = createdAt
    )

    private fun withLinks(response: UploadResponse): UploadResponse {
        val base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().removeSuffix("/")
        val root = "$base/api/v1/uploads/${response.id}"
        return response.copy(viewUrl = "$root/view", downloadUrl = "$root/download")
    }

    private fun resolveTenantId(authentication: Authentication): Long {
        val details = authentication.details
        require(details is Map<*, *>) { "Token inválido: tenantId ausente" }
        return when (val tenantClaim = details["tenantId"]) {
            is Number -> tenantClaim.toLong()
            is String -> tenantClaim.toLongOrNull() ?: error("Token inválido: tenantId ausente")
            else -> error("Token inválido: tenantId ausente")
        }
    }
}
