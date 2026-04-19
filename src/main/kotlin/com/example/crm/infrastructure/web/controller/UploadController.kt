package com.example.crm.infrastructure.web.controller

import com.example.crm.application.port.input.UploadCommand
import com.example.crm.application.port.input.UploadUseCase
import com.example.crm.domain.model.FileType
import com.example.crm.infrastructure.web.dto.response.FileTypeRuleResponse
import com.example.crm.infrastructure.web.dto.response.UploadResponse
import com.example.crm.infrastructure.web.dto.response.UploadRulesResponse
import com.example.crm.infrastructure.web.mapper.UploadWebMapper
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/uploads")
class UploadController(
    private val useCase: UploadUseCase,
    private val mapper: UploadWebMapper
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("fileType") fileType: FileType,
        @RequestParam("tenantId") tenantId: Long,
        @RequestParam("entityId") entityId: Long,
        @RequestParam("width", required = false) width: Int?,
        @RequestParam("height", required = false) height: Int?,
        @RequestParam("quality", required = false) quality: Int?,
        @RequestParam("legend", required = false) legend: String?
    ): ResponseEntity<UploadResponse> {
        require(!file.isEmpty) { "Arquivo obrigatório" }
        val command = UploadCommand(
            content = file.bytes,
            originalFileName = file.originalFilename,
            contentType = file.contentType,
            fileType = fileType,
            tenantId = tenantId,
            entityId = entityId,
            width = width,
            height = height,
            quality = quality,
            legend = legend
        )
        val created = useCase.upload(command)
        return ResponseEntity.created(URI.create("/api/v1/uploads/${created.id}"))
            .body(mapper.toResponse(created))
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
        ResponseEntity.ok(mapper.toResponse(useCase.getById(id)))

    @GetMapping
    fun listByEntity(
        @RequestParam fileType: FileType,
        @RequestParam entityId: Long
    ): ResponseEntity<List<UploadResponse>> =
        ResponseEntity.ok(useCase.listByEntity(fileType, entityId).map { mapper.toResponse(it) })
}
