package com.example.crm.application.port.input

import com.example.crm.application.port.output.UploadSettings
import com.example.crm.domain.model.FileType
import com.example.crm.domain.model.Upload
import java.util.UUID

interface UploadUseCase {
    fun upload(command: UploadCommand): Upload
    fun getById(id: UUID): Upload
    fun update(id: UUID, command: UpdateUploadCommand): Upload
    fun delete(id: UUID)
    // list uploads with optional filters and pagination
    fun list(fileType: FileType?, entityId: Long?, page: Int, size: Int): List<Upload>
    fun getRules(): UploadSettings
}

data class UploadCommand(
    val content: ByteArray,
    val originalFileName: String?,
    val contentType: String?,
    val fileType: FileType,
    val tenantId: Long,
    val entityId: Long,
    val width: Int? = null,
    val height: Int? = null,
    val sortOrder: Int = 0,
    val title: String? = null,
    val subtitle: String? = null,
    val quality: Int? = null
) {
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
}

data class UpdateUploadCommand(
    val fileType: FileType,
    val entityId: Long,
    val sortOrder: Int,
    val title: String? = null,
    val subtitle: String? = null
)
