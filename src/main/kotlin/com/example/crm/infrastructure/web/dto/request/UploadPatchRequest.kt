package com.example.crm.infrastructure.web.dto.request

import com.example.crm.domain.model.FileType

data class UploadPatchRequest(
    val fileType: FileType,
    val entityId: Long,
    val sortOrder: Int,
    val title: String? = null,
    val subtitle: String? = null
)
