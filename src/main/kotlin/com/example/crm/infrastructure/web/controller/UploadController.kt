package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.UploadCommand
import com.example.crm.application.port.input.UploadUseCase
import com.example.crm.domain.model.FileType
import com.example.crm.infrastructure.web.dto.response.FileTypeRuleResponse
import com.example.crm.infrastructure.web.dto.response.UploadResponse
import com.example.crm.infrastructure.web.dto.response.UploadRulesResponse
import com.example.crm.infrastructure.web.mapper.UploadFileResourceResolver
import com.example.crm.infrastructure.web.mapper.UploadWebMapper
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/uploads")
class UploadController(
    private val useCase: UploadUseCase,
    private val mapper: UploadWebMapper,
    private val fileResolver: UploadFileResourceResolver
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("fileType") fileType: FileType,
        @RequestParam("entityId") entityId: Long,
        @RequestParam("sortOrder", required = false, defaultValue = "0") sortOrder: Int,
        @RequestParam("title", required = false) title: String?,
        @RequestParam("subtitle", required = false) subtitle: String?,
        @RequestParam("width", required = false) width: Int?,
        @RequestParam("height", required = false) height: Int?,
        @RequestParam("quality", required = false) quality: Int?,
        @RequestParam("legend", required = false) legend: String?,
        authentication: Authentication
    ): ResponseEntity<UploadResponse> {
        require(!file.isEmpty) { "Arquivo obrigatório" }
        val tenantId = resolveTenantId(authentication)
        val command = UploadCommand(
            content = file.bytes,
            originalFileName = file.originalFilename,
            contentType = file.contentType,
            fileType = fileType,
            tenantId = tenantId,
            entityId = entityId,
            sortOrder = sortOrder,
            title = title,
            subtitle = subtitle,
            width = width,
            height = height,
            quality = quality,
            legend = legend
        )
        val created = useCase.upload(command)
        return ResponseEntity.created(URI.create("/api/v1/uploads/${created.id}"))
            .body(mapper.toResponse(created))
    }

    private fun resolveTenantId(authentication: Authentication): Long {
        val details = authentication.details
        require(details is Map<*, *>) { "Token inválido: tenantId ausente" }
        val tenantClaim = details["tenantId"]
        return when (tenantClaim) {
            is Number -> tenantClaim.toLong()
            is String -> tenantClaim.toLongOrNull() ?: error("Token inválido: tenantId ausente")
            else -> error("Token inválido: tenantId ausente")
        }
    }

    @GetMapping("/rules")
    fun rules(): ResponseEntity<UploadRulesResponse> {
        val s = useCase.getRules()
        val perType = s.allRules().map { (type, rule) ->
            FileTypeRuleResponse(
                fileType = type,
                displayName = type.displayName,
                allowedExtensions = rule.allowedExtensions,
                maxSizeBytes = rule.maxSizeBytes,
                maxWidth = rule.maxWidth,
                maxHeight = rule.maxHeight
            )
        }
        return ResponseEntity.ok(
            UploadRulesResponse(
                minQuality = s.minQuality,
                maxQuality = s.maxQuality,
                rules = perType
            )
        )
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<UploadResponse> =
        ResponseEntity.ok(withLinks(mapper.toResponse(useCase.getById(id))))

    @GetMapping("/{id}/download")
    fun download(@PathVariable id: UUID): ResponseEntity<Resource> {
        val upload = useCase.getById(id)
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

    @GetMapping("/{id}/view")
    fun view(@PathVariable id: UUID): ResponseEntity<Resource> {
        val upload = useCase.getById(id)
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

    @GetMapping
    fun listByEntity(
        @RequestParam(required = false) fileType: FileType?,
        @RequestParam(required = false) entityId: Long?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<List<UploadResponse>> =
        ResponseEntity.ok(
            useCase.list(fileType, entityId, page, size).map { withLinks(mapper.toResponse(it)) }
        )

    private fun withLinks(response: UploadResponse): UploadResponse {
        val base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().removeSuffix("/")
        val root = "$base/api/v1/uploads/${response.id}"
        return response.copy(
            viewUrl = "$root/view",
            downloadUrl = "$root/download"
        )
    }
}
