package com.example.crm.domain.repository

import com.example.crm.domain.model.FileType
import com.example.crm.domain.model.Upload
import java.util.UUID

interface UploadRepository {
    fun save(upload: Upload): Upload
    fun findById(id: UUID): Upload?
    fun findByFileTypeAndEntityId(fileType: FileType, entityId: Long): List<Upload>
    // find with optional filters and pagination
    fun find(fileType: FileType?, entityId: Long?, page: Int, size: Int): List<Upload>
}
