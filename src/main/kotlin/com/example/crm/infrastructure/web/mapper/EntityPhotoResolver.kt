package com.example.crm.infrastructure.web.mapper

import com.example.crm.application.port.input.UploadUseCase
import org.springframework.stereotype.Component

@Component
class EntityPhotoResolver(
    private val uploadUseCase: UploadUseCase
) {
    fun resolve(entityId: Long): String? =
        uploadUseCase.list(fileType = null, entityId = entityId, page = 0, size = 100)
            .asSequence()
            .filter { it.filePath.startsWith("/uploads/") }
            .maxByOrNull { it.createdAt }
            ?.filePath
}
