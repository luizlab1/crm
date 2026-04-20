package com.example.crm.infrastructure.web.dto.response

import com.example.crm.domain.model.FileType
import java.time.OffsetDateTime
import java.util.UUID

data class UploadResponse(
    val id: UUID,
    val fileType: FileType,
    val entityId: Long,
    val tenantId: Long,
    val itemId: Long?,
    val categoryId: Long?,
    val customerId: Long?,
    val workerId: Long?,
    val fileName: String,
    val filePath: String,
    val contentType: String,
    val size: Long,
    val width: Int?,
    val height: Int?,
    val sortOrder: Int,
    val title: String?,
    val subtitle: String?,
    val legend: String?,
    val createdAt: OffsetDateTime,
    val viewUrl: String? = null,
    val downloadUrl: String? = null
)
