package com.example.crm.application.usecase

import com.example.crm.application.port.input.UploadCommand
import com.example.crm.application.port.input.UpdateUploadCommand
import com.example.crm.application.port.input.UploadUseCase
import com.example.crm.application.port.output.FileStorage
import com.example.crm.application.port.output.StoreFileCommand
import com.example.crm.application.port.output.UploadSettings
import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.FileType
import com.example.crm.domain.model.Upload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale
import java.util.UUID

@Service
@Transactional
class UploadUseCaseImpl(
    private val uploadRepository: com.example.crm.domain.repository.UploadRepository,
    private val fileStorage: FileStorage,
    private val settings: UploadSettings
) : UploadUseCase {

    override fun upload(command: UploadCommand): Upload {
        validate(command)
        val extension = resolveExtension(command)
        val stored = fileStorage.store(
            StoreFileCommand(
                content = command.content,
                extension = extension,
                contentType = command.contentType ?: "application/octet-stream",
                fileType = command.fileType,
                tenantId = command.tenantId,
                entityId = command.entityId,
                targetWidth = command.width,
                targetHeight = command.height,
                quality = command.quality
            )
        )
        val upload = Upload(
            fileType = command.fileType,
            entityId = command.entityId,
            tenantId = command.tenantId,
            itemId = if (command.fileType.isItem()) command.entityId else null,
            categoryId = if (command.fileType == FileType.CATEGORY) command.entityId else null,
            customerId = if (command.fileType == FileType.CUSTOMER) command.entityId else null,
            workerId = if (command.fileType == FileType.WORKER) command.entityId else null,
            fileName = stored.fileName,
            filePath = stored.filePath,
            contentType = stored.contentType,
            size = stored.size,
            width = stored.width,
            height = stored.height,
            sortOrder = command.sortOrder,
            title = command.title?.trim()?.takeIf { it.isNotBlank() },
            subtitle = command.subtitle?.trim()?.takeIf { it.isNotBlank() }
        )
        return uploadRepository.save(upload)
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): Upload =
        uploadRepository.findById(id) ?: throw EntityNotFoundException("Upload", id)

    override fun update(id: UUID, command: UpdateUploadCommand): Upload {
        require(command.sortOrder >= 0) { "sortOrder inválido (mínimo: 0)" }
        val current = uploadRepository.findById(id) ?: throw EntityNotFoundException("Upload", id)
        val updated = current.copy(
            fileType = command.fileType,
            entityId = command.entityId,
            itemId = if (command.fileType.isItem()) command.entityId else null,
            categoryId = if (command.fileType == FileType.CATEGORY) command.entityId else null,
            customerId = if (command.fileType == FileType.CUSTOMER) command.entityId else null,
            workerId = if (command.fileType == FileType.WORKER) command.entityId else null,
            sortOrder = command.sortOrder,
            title = command.title?.trim()?.takeIf { it.isNotBlank() },
            subtitle = command.subtitle?.trim()?.takeIf { it.isNotBlank() }
        )
        return uploadRepository.save(updated)
    }

    override fun delete(id: UUID) {
        uploadRepository.findById(id) ?: throw EntityNotFoundException("Upload", id)
        uploadRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun list(fileType: FileType?, entityId: Long?, page: Int, size: Int): List<Upload> =
        uploadRepository.find(fileType, entityId, page, size)

    override fun getRules(): UploadSettings = settings

    private fun validate(command: UploadCommand) {
        require(command.content.isNotEmpty()) { "Arquivo vazio" }
        val rule = settings.ruleFor(command.fileType)
        require(command.content.size.toLong() <= rule.maxSizeBytes) {
            "Arquivo excede o tamanho máximo de ${rule.maxSizeBytes} bytes"
        }
        val extension = resolveExtension(command)
        require(extension in rule.allowedExtensions) {
            "Extensão não permitida. Permitidas: ${rule.allowedExtensions.joinToString(", ")}"
        }
        command.width?.let {
            require(it in MIN_DIMENSION..rule.maxWidth) { "Largura inválida ($MIN_DIMENSION..${rule.maxWidth})" }
        }
        command.height?.let {
            require(it in MIN_DIMENSION..rule.maxHeight) { "Altura inválida ($MIN_DIMENSION..${rule.maxHeight})" }
        }
        require(command.sortOrder >= 0) { "sortOrder inválido (mínimo: 0)" }
        command.quality?.let {
            require(it in settings.minQuality..settings.maxQuality) {
                "Quality inválido (${settings.minQuality}..${settings.maxQuality})"
            }
        }
    }

    companion object {
        private const val MIN_DIMENSION: Int = 1
    }

    private fun resolveExtension(command: UploadCommand): String {
        val fromName = command.originalFileName
            ?.substringAfterLast('.', missingDelimiterValue = "")
            ?.lowercase(Locale.ROOT)
            ?.takeIf { it.isNotBlank() }
        val fromContentType = when (command.contentType?.lowercase(Locale.ROOT)) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> null
        }
        return fromName ?: fromContentType ?: ""
    }

    private fun FileType.isItem(): Boolean = this == FileType.PRODUCT || this == FileType.SERVICE
}
