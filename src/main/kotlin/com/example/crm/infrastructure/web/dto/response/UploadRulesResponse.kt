package com.example.crm.infrastructure.web.dto.response

import com.example.crm.domain.model.FileType

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
