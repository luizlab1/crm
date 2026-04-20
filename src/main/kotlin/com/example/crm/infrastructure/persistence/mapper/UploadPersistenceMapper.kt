package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.Upload
import com.example.crm.infrastructure.persistence.entity.UploadJpaEntity
import org.springframework.stereotype.Component

@Component
class UploadPersistenceMapper {
    fun toDomain(e: UploadJpaEntity) = Upload(
        id = e.id, fileType = e.fileType, entityId = e.entityId, tenantId = e.tenantId,
        itemId = e.itemId, categoryId = e.categoryId,
        customerId = e.customerId, workerId = e.workerId,
        fileName = e.fileName, filePath = e.filePath, contentType = e.contentType,
        size = e.size, width = e.width, height = e.height, sortOrder = e.sortOrder,
        title = e.title, subtitle = e.subtitle, legend = e.legend,
        createdAt = e.createdAt
    )

    fun toEntity(d: Upload) = UploadJpaEntity(
        id = d.id, fileType = d.fileType, entityId = d.entityId, tenantId = d.tenantId,
        itemId = d.itemId, categoryId = d.categoryId,
        customerId = d.customerId, workerId = d.workerId,
        fileName = d.fileName, filePath = d.filePath, contentType = d.contentType,
        size = d.size, width = d.width, height = d.height, sortOrder = d.sortOrder,
        title = d.title, subtitle = d.subtitle, legend = d.legend,
        createdAt = d.createdAt
    )
}
