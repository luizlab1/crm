package com.example.crm.dto.request

import com.example.crm.entity.FileType

data class UploadPatchRequest(
    val fileType: FileType,
    val entityId: Long,
    val sortOrder: Int,
    val title: String? = null,
    val subtitle: String? = null
)
