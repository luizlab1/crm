package com.example.crm.infrastructure.web.mapper

import com.example.crm.application.port.input.UploadUseCase
import com.example.crm.domain.model.FileType
import com.example.crm.domain.model.ItemType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Component
class ItemPhotosResolver(
    private val uploadUseCase: UploadUseCase
) {
    fun resolve(itemType: ItemType, itemId: Long): List<String> {
        val fileType = itemType.toFileType() ?: return emptyList()
        val base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().removeSuffix("/")
        return uploadUseCase.list(fileType = fileType, entityId = itemId, page = 0, size = 100)
            .asSequence()
            .filter { it.filePath.startsWith("/uploads/") }
            .map { "$base/api/v1/uploads/${it.id}/view" }
            .distinct()
            .toList()
    }

    private fun ItemType.toFileType(): FileType? =
        when (this) {
            ItemType.PRODUCT -> FileType.PRODUCT
            ItemType.SERVICE -> FileType.SERVICE
        }
}
