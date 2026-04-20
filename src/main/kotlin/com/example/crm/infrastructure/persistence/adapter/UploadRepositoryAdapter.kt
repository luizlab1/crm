package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.FileType
import com.example.crm.domain.model.Upload
import com.example.crm.domain.repository.UploadRepository
import com.example.crm.infrastructure.persistence.mapper.UploadPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.UploadJpaRepository
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Component
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.UUID

@Component
class UploadRepositoryAdapter(
    private val jpa: UploadJpaRepository,
    private val mapper: UploadPersistenceMapper
) : UploadRepository {

    override fun save(upload: Upload): Upload =
        mapper.toDomain(jpa.save(mapper.toEntity(upload)))

    override fun findById(id: UUID): Upload? =
        jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)

    override fun deleteById(id: UUID) {
        jpa.deleteById(id)
    }

    override fun findByFileTypeAndEntityId(fileType: FileType, entityId: Long): List<Upload> =
        jpa.findOrderedByFileTypeAndEntityId(fileType, entityId).map { mapper.toDomain(it) }

    // implementation of domain repository method for optional filters and pagination
    override fun find(fileType: FileType?, entityId: Long?, page: Int, size: Int): List<Upload> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("createdAt")))
        val pageResult = when {
            fileType != null && entityId != null ->
                jpa.findOrderedByFileTypeAndEntityId(fileType, entityId).let { PageImpl(it) }
            fileType != null -> jpa.findByFileType(fileType, pageable)
            entityId != null -> jpa.findByEntityId(entityId, pageable)
            else -> jpa.findAll(pageable)
        }
        return pageResult.map { mapper.toDomain(it) }.toList()
    }
}
