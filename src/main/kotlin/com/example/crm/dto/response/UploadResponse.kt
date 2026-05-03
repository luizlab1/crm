package com.example.crm.dto.response

import com.example.crm.entity.FileType
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
    val createdAt: OffsetDateTime,
    val viewUrl: String? = null,
    val downloadUrl: String? = null
)

data class FileTypeRuleResponse(
    val fileType: FileType,
    val displayName: String,
    val allowedExtensions: Set<String>,
    val maxSizeBytes: Long,
    val maxWidth: Int,
    val maxHeight: Int
)

data class UploadRulesResponse(
    val minQuality: Int,
    val maxQuality: Int,
    val rules: List<FileTypeRuleResponse>
)
