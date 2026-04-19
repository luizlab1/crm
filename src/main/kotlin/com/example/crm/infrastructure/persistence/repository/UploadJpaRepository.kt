package com.example.crm.infrastructure.persistence.repository

import com.example.crm.domain.model.FileType
import com.example.crm.infrastructure.persistence.entity.UploadJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UploadJpaRepository : JpaRepository<UploadJpaEntity, UUID> {
    fun findByFileTypeAndEntityId(fileType: FileType, entityId: Long): List<UploadJpaEntity>
}
