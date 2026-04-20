package com.example.crm.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Upload(
    val id: UUID = UUID.randomUUID(),
    val fileType: FileType,
    val entityId: Long,
    val tenantId: Long,
    val itemId: Long? = null,
    val categoryId: Long? = null,
    val customerId: Long? = null,
    val workerId: Long? = null,
    val fileName: String,
    val filePath: String,
    val contentType: String,
    val size: Long,
    val width: Int? = null,
    val height: Int? = null,
    val sortOrder: Int = 0,
    val title: String? = null,
    val subtitle: String? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
