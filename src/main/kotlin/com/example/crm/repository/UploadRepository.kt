package com.example.crm.repository

import com.example.crm.entity.FileType
import com.example.crm.entity.UploadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UploadRepository : JpaRepository<UploadEntity, UUID> {
    @Query(
        """
        select u from UploadEntity u
        where u.fileType = :fileType and u.entityId = :entityId
        order by u.sortOrder asc, u.createdAt asc
        """
    )
    fun findOrderedByFileTypeAndEntityId(
        @Param("fileType") fileType: FileType,
        @Param("entityId") entityId: Long
    ): List<UploadEntity>

    fun findByFileType(fileType: FileType, pageable: Pageable): Page<UploadEntity>
    fun findByEntityId(entityId: Long, pageable: Pageable): Page<UploadEntity>
}
