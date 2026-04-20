package com.example.crm.infrastructure.persistence.repository

import com.example.crm.domain.model.FileType
import com.example.crm.infrastructure.persistence.entity.UploadJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UploadJpaRepository : JpaRepository<UploadJpaEntity, UUID> {
    @Query(
        """
        select u from UploadJpaEntity u
        where u.fileType = :fileType and u.entityId = :entityId
        order by u.sortOrder asc, u.createdAt asc
        """
    )
    fun findOrderedByFileTypeAndEntityId(
        @Param("fileType") fileType: FileType,
        @Param("entityId") entityId: Long
    ): List<UploadJpaEntity>
    fun findByFileType(fileType: FileType, pageable: Pageable): Page<UploadJpaEntity>
    fun findByEntityId(entityId: Long, pageable: Pageable): Page<UploadJpaEntity>
}
