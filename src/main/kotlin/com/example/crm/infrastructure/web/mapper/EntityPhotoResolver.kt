package com.example.crm.infrastructure.web.mapper

import com.example.crm.application.port.input.UploadUseCase
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Component
class EntityPhotoResolver(
    private val uploadUseCase: UploadUseCase
) {
    fun resolve(entityId: Long): String? =
        uploadUseCase.list(fileType = null, entityId = entityId, page = 0, size = 100)
            .asSequence()
            .filter { it.filePath.startsWith("/uploads/") }
            .maxByOrNull { it.createdAt }
            ?.id
            ?.let { uploadId ->
                val base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().removeSuffix("/")
                "$base/api/v1/uploads/$uploadId/view"
            }
}
