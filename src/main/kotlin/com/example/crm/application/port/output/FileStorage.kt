package com.example.crm.application.port.output

import com.example.crm.domain.model.FileType

interface FileStorage {
    fun store(command: StoreFileCommand): StoredFile
}

data class StoreFileCommand(
    val content: ByteArray,
    val extension: String,
    val contentType: String,
    val fileType: FileType,
    val tenantId: Long,
    val entityId: Long,
    val targetWidth: Int? = null,
    val targetHeight: Int? = null,
    val quality: Int? = null
) {
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
}

data class StoredFile(
    val fileName: String,
    val filePath: String,
    val contentType: String,
    val size: Long,
    val width: Int?,
    val height: Int?
)
