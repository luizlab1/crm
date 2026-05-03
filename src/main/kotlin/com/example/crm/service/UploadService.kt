package com.example.crm.service

import com.example.crm.application.port.output.FileStorage
import com.example.crm.application.port.output.StoreFileCommand
import com.example.crm.application.port.output.UploadSettings
import com.example.crm.entity.FileType
import com.example.crm.entity.UploadEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.UploadRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale
import java.util.UUID

@Service
@Transactional
class UploadService(
    private val uploadRepository: UploadRepository,
    private val fileStorage: FileStorage,
    private val settings: UploadSettings
) {

    fun upload(
        content: ByteArray,
        originalFileName: String?,
        contentType: String?,
        fileType: FileType,
        tenantId: Long,
        entityId: Long,
        width: Int? = null,
        height: Int? = null,
        sortOrder: Int = 0,
        title: String? = null,
        subtitle: String? = null,
        quality: Int? = null
    ): UploadEntity {
        validate(content, originalFileName, contentType, fileType, width, height, sortOrder, quality)
        val extension = resolveExtension(originalFileName, contentType)
        val stored = fileStorage.store(
            StoreFileCommand(
                content = content,
                extension = extension,
                contentType = contentType ?: "application/octet-stream",
                fileType = fileType,
                tenantId = tenantId,
                entityId = entityId,
                targetWidth = width,
                targetHeight = height,
                quality = quality
            )
        )
        val entity = UploadEntity(
            fileType = fileType,
            entityId = entityId,
            tenantId = tenantId,
            itemId = if (fileType.isItem()) entityId else null,
            categoryId = if (fileType == FileType.CATEGORY) entityId else null,
            customerId = if (fileType == FileType.CUSTOMER) entityId else null,
            workerId = if (fileType == FileType.WORKER) entityId else null,
            fileName = stored.fileName,
            filePath = stored.filePath,
            contentType = stored.contentType,
            size = stored.size,
            width = stored.width,
            height = stored.height,
            sortOrder = sortOrder,
            title = title?.trim()?.takeIf { it.isNotBlank() },
            subtitle = subtitle?.trim()?.takeIf { it.isNotBlank() }
        )
        return uploadRepository.save(entity)
    }

    @Transactional(readOnly = true)
    fun getById(id: UUID): UploadEntity =
        uploadRepository.findById(id).orElseThrow { EntityNotFoundException("Upload", id) }

    fun update(id: UUID, fileType: FileType, entityId: Long, sortOrder: Int, title: String?, subtitle: String?): UploadEntity {
        require(sortOrder >= 0) { "sortOrder inválido (mínimo: 0)" }
        val current = getById(id)
        current.fileType = fileType
        current.entityId = entityId
        current.itemId = if (fileType.isItem()) entityId else null
        current.categoryId = if (fileType == FileType.CATEGORY) entityId else null
        current.customerId = if (fileType == FileType.CUSTOMER) entityId else null
        current.workerId = if (fileType == FileType.WORKER) entityId else null
        current.sortOrder = sortOrder
        current.title = title?.trim()?.takeIf { it.isNotBlank() }
        current.subtitle = subtitle?.trim()?.takeIf { it.isNotBlank() }
        return uploadRepository.save(current)
    }

    fun delete(id: UUID) {
        getById(id)
        uploadRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun list(fileType: FileType?, entityId: Long?, page: Int, size: Int): List<UploadEntity> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("createdAt")))
        return when {
            fileType != null && entityId != null ->
                uploadRepository.findOrderedByFileTypeAndEntityId(fileType, entityId)
            fileType != null -> uploadRepository.findByFileType(fileType, pageable).content
            entityId != null -> uploadRepository.findByEntityId(entityId, pageable).content
            else -> uploadRepository.findAll(pageable).content
        }
    }

    fun getRules(): UploadSettings = settings

    private fun validate(
        content: ByteArray,
        originalFileName: String?,
        contentType: String?,
        fileType: FileType,
        width: Int?,
        height: Int?,
        sortOrder: Int,
        quality: Int?
    ) {
        require(content.isNotEmpty()) { "Arquivo vazio" }
        val rule = settings.ruleFor(fileType)
        require(content.size.toLong() <= rule.maxSizeBytes) {
            "Arquivo excede o tamanho máximo de ${rule.maxSizeBytes} bytes"
        }
        val extension = resolveExtension(originalFileName, contentType)
        require(extension in rule.allowedExtensions) {
            "Extensão não permitida. Permitidas: ${rule.allowedExtensions.joinToString(", ")}"
        }
        width?.let { require(it in 1..rule.maxWidth) { "Largura inválida (1..${rule.maxWidth})" } }
        height?.let { require(it in 1..rule.maxHeight) { "Altura inválida (1..${rule.maxHeight})" } }
        require(sortOrder >= 0) { "sortOrder inválido (mínimo: 0)" }
        quality?.let {
            require(it in settings.minQuality..settings.maxQuality) {
                "Quality inválido (${settings.minQuality}..${settings.maxQuality})"
            }
        }
    }

    private fun resolveExtension(originalFileName: String?, contentType: String?): String {
        val fromName = originalFileName
            ?.substringAfterLast('.', missingDelimiterValue = "")
            ?.lowercase(Locale.ROOT)
            ?.takeIf { it.isNotBlank() }
        val fromContentType = when (contentType?.lowercase(Locale.ROOT)) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> null
        }
        return fromName ?: fromContentType ?: ""
    }

    private fun FileType.isItem(): Boolean = this == FileType.PRODUCT || this == FileType.SERVICE
}
