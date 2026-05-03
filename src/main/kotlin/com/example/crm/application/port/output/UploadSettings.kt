package com.example.crm.application.port.output

import com.example.crm.entity.FileType

data class FileTypeRule(
    val allowedExtensions: Set<String>,
    val maxSizeBytes: Long,
    val maxWidth: Int,
    val maxHeight: Int
)

data class UploadSettings(
    val minQuality: Int,
    val maxQuality: Int,
    val defaults: FileTypeRule,
    val rules: Map<FileType, FileTypeRule>
) {
    fun ruleFor(type: FileType): FileTypeRule = rules[type] ?: defaults
    fun allRules(): Map<FileType, FileTypeRule> =
        FileType.entries.associateWith { ruleFor(it) }
}
