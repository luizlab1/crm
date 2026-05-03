package com.example.crm.infrastructure.config

import com.example.crm.entity.FileType
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.upload")
data class UploadProperties(
    val baseDirectory: String = "./var/uploads",
    val minQuality: Int = DEFAULT_MIN_QUALITY,
    val maxQuality: Int = DEFAULT_MAX_QUALITY,
    val defaults: RuleProperties = RuleProperties(),
    val rules: Map<FileType, RuleProperties> = emptyMap()
) {
    data class RuleProperties(
        val allowedExtensions: Set<String> = setOf("jpg", "jpeg", "png", "webp"),
        val maxSizeBytes: Long = DEFAULT_MAX_SIZE_BYTES,
        val maxWidth: Int = DEFAULT_MAX_DIMENSION,
        val maxHeight: Int = DEFAULT_MAX_DIMENSION
    )

    companion object {
        const val DEFAULT_MAX_SIZE_BYTES: Long = 10L * 1024 * 1024
        const val DEFAULT_MAX_DIMENSION: Int = 8000
        const val DEFAULT_MIN_QUALITY: Int = 1
        const val DEFAULT_MAX_QUALITY: Int = 100
    }
}
