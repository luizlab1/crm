package com.example.crm.infrastructure.persistence.repository

import com.example.crm.domain.model.FileType
import com.example.crm.infrastructure.persistence.entity.UploadJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UploadJpaRepository : JpaRepository<UploadJpaEntity, UUID> {
    fun findByFileTypeAndEntityId(fileType: FileType, entityId: Long): List<UploadJpaEntity>
    fun findByFileType(fileType: FileType, pageable: Pageable): Page<UploadJpaEntity>
    fun findByEntityId(entityId: Long, pageable: Pageable): Page<UploadJpaEntity>
}
